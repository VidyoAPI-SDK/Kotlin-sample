package com.vidyo.vidyoconnector.bl.connector.network

import com.vidyo.VidyoClient.Connector.Connector
import com.vidyo.VidyoClient.NetworkInterface
import com.vidyo.vidyoconnector.bl.connector.ConnectorScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworksManager(private val scope: ConnectorScope) {
    private val networksState = MutableStateFlow(emptySet<Network>())
    private val networkForMediaState = MutableStateFlow(Network.Null)
    private val networkForSignalingState = MutableStateFlow(Network.Null)

    val networks = networksState.asStateFlow()
    val networkForMedia = networkForMediaState.asStateFlow()
    val networkForSignaling = networkForSignalingState.asStateFlow()

    init {
        scope.connector.registerNetworkInterfaceEventListener(InterfacesEventListener())
        scope.connector.getActiveNetworkInterface(GetActiveNetworkInterface())
    }

    fun selectNetworkForMedia(network: Network) {
        val handle = network.handle ?: return
        if (scope.connector.selectNetworkInterfaceForMedia(handle)) {
            networkForMediaState.value = network
        }
    }

    fun selectNetworkForSignaling(network: Network) {
        val handle = network.handle ?: return
        if (scope.connector.selectNetworkInterfaceForSignaling(handle)) {
            networkForSignalingState.value = network
        }
    }

    private inner class GetActiveNetworkInterface : Connector.IGetActiveNetworkInterface {
        override fun onGetActiveNetworkInterface(
            signalingInterface: NetworkInterface,
            mediaInterface: NetworkInterface
        ) {
            networkForMediaState.value = Network.from(mediaInterface)
            networkForSignalingState.value = Network.from(signalingInterface)
        }
    }

    private inner class InterfacesEventListener : Connector.IRegisterNetworkInterfaceEventListener {
        override fun onNetworkInterfaceAdded(networkInterface: NetworkInterface) = scope.run {
            val set = networksState.value.toMutableSet()
            if (set.add(Network.from(networkInterface))) {
                networksState.value = set
            }
        }

        override fun onNetworkInterfaceRemoved(networkInterface: NetworkInterface) = scope.run {
            val set = networksState.value.toMutableSet()
            if (set.remove(Network.from(networkInterface))) {
                networksState.value = set
            }
        }

        override fun onNetworkInterfaceSelected(
            networkInterface: NetworkInterface,
            networkTransportType: NetworkInterface.NetworkInterfaceTransportType
        ) {
        }

        override fun onNetworkInterfaceStateUpdated(
            networkInterface: NetworkInterface,
            networkState: NetworkInterface.NetworkInterfaceState
        ) {
        }
    }
}
