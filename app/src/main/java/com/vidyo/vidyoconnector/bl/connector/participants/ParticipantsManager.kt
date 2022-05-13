package com.vidyo.vidyoconnector.bl.connector.participants

import com.vidyo.VidyoClient.Connector.Connector
import com.vidyo.vidyoconnector.bl.connector.ConnectorScope
import com.vidyo.vidyoconnector.bl.connector.conference.ConferenceManager
import com.vidyo.vidyoconnector.utils.coroutines.collectInScope
import com.vidyo.vidyoconnector.utils.coroutines.trigger
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import com.vidyo.VidyoClient.Endpoint.Participant as VcParticipant

class ParticipantsManager(private val scope: ConnectorScope) {
    private val map = HashMap<String, Participant>()
    private val mapTrigger = MutableStateFlow(0L)
    private val allState = MutableStateFlow(emptyList<Participant>())

    private val pinnedState = MutableStateFlow("")
    private val onJoinedEvent = MutableSharedFlow<Participant>(extraBufferCapacity = 64)
    private val onLeftEvent = MutableSharedFlow<Participant>(extraBufferCapacity = 64)

    val all = allState.asStateFlow()
    val pinned = pinnedState.asStateFlow()
    val onJoined = onJoinedEvent.asSharedFlow()
    val onLeft = onLeftEvent.asSharedFlow()

    init {
        scope.connector.reportLocalParticipantOnJoined(true)
        scope.connector.registerParticipantEventListener(ParticipantEventListener())

        mapTrigger.debounce(500).collectInScope(scope) {
            val temp = map.values.toMutableList()
            temp.sortBy { p -> p.name }
            allState.value = temp
        }
    }

    fun trackParticipantAvailable(id: String): Flow<Boolean> {
        return mapTrigger.map { map.containsKey(id) }.distinctUntilChanged()
    }

    suspend fun findParticipant(id: String): Participant? {
        return withContext(scope.dispatcher) { map[id] }
    }

    fun setParticipantPinned(participant: Participant, pin: Boolean) = scope.run {
        if (!scope.connector.pinParticipant(participant.handle, pin)) {
            return@run
        }
        if (pin) {
            pinnedState.value = participant.id
        } else if (pinnedState.value == participant.id) {
            pinnedState.value = ""
        }
    }

    private inner class ParticipantEventListener : Connector.IRegisterParticipantEventListener {
        override fun onParticipantJoined(participant: VcParticipant) {
            scope.run {
                val other = Participant.from(participant)
                map[other.id] = other
                mapTrigger.trigger()
                onJoinedEvent.tryEmit(other)
            }
        }

        override fun onParticipantLeft(participant: VcParticipant) {
            scope.run {
                val other = Participant.from(participant)
                if (pinnedState.value == other.id) {
                    pinnedState.value = ""
                }
                map.remove(other.id)
                mapTrigger.trigger()
                onLeftEvent.tryEmit(other)
            }
        }

        override fun onDynamicParticipantChanged(participants: ArrayList<VcParticipant>) {
            scope.run {
            }
        }

        override fun onLoudestParticipantChanged(participant: VcParticipant, audioOnly: Boolean) {
        }
    }
}
