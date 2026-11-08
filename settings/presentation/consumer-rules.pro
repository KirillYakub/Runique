# ---- Navigation Compose type-safe routes ----
# @Serializable route objects: keep serializers resolvable and route names stable
# across process death (saved back stack stores the route class name).
-keep @kotlinx.serialization.Serializable class com.kiras.settings.presentation.util.** { *; }
