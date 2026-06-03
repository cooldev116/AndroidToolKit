# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ===============================
# Android 四大组件
# ===============================

# Activity
-keep public class * extends android.app.Activity
-keep class * extends androidx.appcompat.app.AppCompatActivity

# Service
-keep public class * extends android.app.Service

# BroadcastReceiver
-keep public class * extends android.content.BroadcastReceiver

# ContentProvider
-keep public class * extends android.content.ContentProvider

# Application
-keep public class * extends android.app.Application

# Fragment
-keep public class * extends androidx.fragment.app.Fragment

# 自定义 View（在 XML 中引用）
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# JNI
-keepclasseswithmembernames class * {
    native <methods>;
}

# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }

# OkHttp
-dontwarn okhttp3.**

# Kotlin
-keep class kotlin.Metadata { *; }

# ViewBinding
-keepclassmembers class * implements androidx.viewbinding.ViewBinding {
    public static *** inflate(...);
    public static *** bind(...);
}

# 保留所有枚举
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class androidx.databinding.** { *; }
-keep enum * { *; }

# model类
-keep class com.snowflake.toolkit.entity.** {*;}