###############################################################################
#   AETHER SECURITY SHIELD — PROGUARD ENTERPRISE RULESET v3.1
#   100% Code Obfuscation + Anti-Reverse Engineering
###############################################################################

# ─── OPTIMIZATION ENGINE ────────────────────────────────────────────────────
-optimizationpasses 7
-allowaccessmodification
-repackageclasses 'a'
-overloadaggressively
-mergeinterfacesaggressively
-optimizations !code/simplification/cast,!field/*,!class/merging/horizontal

# ─── SOURCE MAP STRIPPING ────────────────────────────────────────────────────
# Attackers cannot trace decompiled code back to original file names
-renamesourcefileattribute ''
-keepattributes !SourceFile,!SourceDir,!LineNumberTable

# ─── CRITICAL: PRESERVE ONLY WHAT MUST BE PRESERVED ────────────────────────

# Android framework entry points
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keepclassmembers class * extends android.content.Context {
    public void *(android.view.View);
}

# Custom Views (Canvas drawing logic must survive obfuscation)
-keep public class com.estrano.starter.view.** { *; }

# Data Models (Gson serialization requires field names intact)
-keepclassmembers class com.estrano.starter.model.** { *; }

# Lifecycle components
-keep class androidx.lifecycle.** { *; }
-keep interface androidx.lifecycle.** { *; }

# View Binding (required for reflection-based inflation)
-keepclassmembers class * implements androidx.viewbinding.ViewBinding {
    public static *** inflate(android.view.LayoutInflater);
    public static *** inflate(android.view.LayoutInflater, android.view.ViewGroup, boolean);
}

# ─── SECURITY MANAGER: NEVER OBFUSCATE THE SHIELD ITSELF ───────────────────
-keep class com.estrano.starter.session.SecurityManager { *; }
-keep interface com.estrano.starter.session.SecurityManager$* { *; }

# ─── GOOGLE LIBRARIES ────────────────────────────────────────────────────────
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# ─── RETROFIT / GSON NETWORK ────────────────────────────────────────────────
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keep,allowobfuscation @interface com.google.gson.annotations.SerializedName
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ─── ANNOTATIONS ─────────────────────────────────────────────────────────────
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# ─── DICTIONARY-BASED RENAMING ───────────────────────────────────────────────
# Renames classes to meaningless single letters (a, b, c, aa, ab...)
-classobfuscationdictionary proguard-dictionary.txt
-obfuscationdictionary proguard-dictionary.txt
-packageobfuscationdictionary proguard-dictionary.txt

# ─── SUPPRESS WARNINGS ────────────────────────────────────────────────────────
-dontwarn sun.misc.**
-dontwarn java.lang.invoke.**
-dontwarn okio.**
