rootProject.name = "VidyoConnector"

include(":app")

val extension = gradle as ExtensionAware
extension.extra["VC_DEPENDENCIES"] = listOf(
    files("app/libs/VidyoClient.aar"),
    files("app/libs/banuba_effect_player_c_api-release.aar"),
)
