-keep class com.jenugumpu.model.** { *; }
-keep class com.jenugumpu.data.** { *; }
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* <fields>;
}
