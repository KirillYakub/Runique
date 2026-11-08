# ---- Splitline app-level R8 rules ----
# Module-specific rules live in each library's consumer-rules.pro.

# Readable crash stack traces (retrace with build/outputs/mapping/release/mapping.txt)
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Generic signatures / annotations used by kotlinx-serialization and Navigation type-safe routes
-keepattributes Signature,InnerClasses,EnclosingMethod,*Annotation*
