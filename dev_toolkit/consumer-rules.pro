# Android_CN_OAID
-keep class com.github.gzuliyujiang.oaid.** { *; }
-keep interface com.github.gzuliyujiang.oaid.** { *; }

# Retrofit / OkHttp / Gson（宿主 ApiService 接口与实体需保留）
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keep class com.google.gson.** { *; }
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
