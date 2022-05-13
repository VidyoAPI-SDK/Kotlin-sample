rootProject.name = "VidyoConnector"

include(":app")

(gradle as ExtensionAware).extra["VC_DEPENDENCIES"] = listOf(
    files("app/libs/VidyoClient.aar"),
    files("app/libs/banuba_effect_player_c_api-release.aar"),
)
