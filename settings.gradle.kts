rootProject.name = "VidyoConnector"

include(":app")

val extension = gradle as ExtensionAware
extension.extra["VC_DEFAULT_GOOGLE_ANALYTICS_ID"] = "G-B3J86W90SG"
extension.extra["VC_DEFAULT_GOOGLE_ANALYTICS_KEY"] = "y_UGAwkhT4aoSGqRvsqnbg"
extension.extra["VC_DEPENDENCIES"] = listOf(
    files("app/libs/VidyoClient.aar"),
    files("app/libs/banuba_effect_player_c_api-release.aar"),
)
