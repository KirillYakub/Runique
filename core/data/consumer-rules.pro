# ---- kotlinx-serialization (persisted session JSON in EncryptedSharedPreferences) ----
# JSON keys are string literals in the generated descriptor, so obfuscation never changes stored data;
# these rules keep serializer lookup (serializer<T>() / Companion.serializer()) working under R8 full mode.
-keepclassmembers @kotlinx.serialization.Serializable class com.kiras.** {
    static ** Companion;
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.kiras.**$$serializer { *; }
-if @kotlinx.serialization.Serializable class com.kiras.**
-keepclassmembers class com.kiras.**$Companion {
    kotlinx.serialization.KSerializer serializer(...);
}
