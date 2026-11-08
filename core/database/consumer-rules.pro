# ---- Room ----
# Room.databaseBuilder() instantiates the generated <Database>_Impl reflectively.
# Room ships its own rules; this is an explicit safety net.
-keep class * extends androidx.room.RoomDatabase { <init>(); }
-keep class com.kiras.core.database.RunDatabase_Impl { <init>(); }
# Entities / DAOs need no keeps: generated code references them directly.
