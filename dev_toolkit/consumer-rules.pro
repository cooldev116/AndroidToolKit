# ============================================================
# Dev Toolkit — consumer ProGuard / R8
# 随 AAR 打包，宿主开启 minify 时自动合并，无需再抄一份。
# ============================================================

# -------------------- 通用属性 --------------------
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes Exceptions
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Kotlin
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-dontwarn kotlinx.**

# -------------------- 本库公开 API --------------------
# 宿主直接调用 / 继承基类 / Gson 反序列化 / Manifest 组件名均依赖完整类名与成员
-keep class com.snowflake.toolkit.** { *; }
-keep interface com.snowflake.toolkit.** { *; }
-keep enum com.snowflake.toolkit.** { *; }

# AndroidX Startup：InitializationProvider 按 meta-data 类名反射加载
-keep class com.snowflake.toolkit.initializer.ToolKitInitializer { *; }

# BaseVBAdapter 等通过 ParameterizedType 解析泛型 ViewBinding，需保留签名
-keepclassmembers class * extends com.snowflake.toolkit.base.BaseVBAdapter {
    <init>(...);
}

# ViewBinding inflate / bind（基类反射或代码生成调用）
-keepclassmembers class * implements androidx.viewbinding.ViewBinding {
    public static *** inflate(...);
    public static *** bind(...);
}

# Parcelable（MMKVUtil / Bundle 扩展）
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# -------------------- Retrofit / OkHttp / Gson --------------------
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

# -------------------- Android_CN_OAID --------------------
-keep class com.github.gzuliyujiang.oaid.** { *; }
-keep interface com.github.gzuliyujiang.oaid.** { *; }
-dontwarn com.github.gzuliyujiang.oaid.**

# -------------------- 依赖库（本模块 api/implementation 传递） --------------------
# AgentWeb
-keep class com.just.agentweb.** { *; }
-dontwarn com.just.agentweb.**

# MMKV
-keep class com.tencent.mmkv.** { *; }
-dontwarn com.tencent.mmkv.**

# ImmersionBar
-keep class com.gyf.immersionbar.** { *; }
-dontwarn com.gyf.immersionbar.**

# AndroidAutoSize
-keep class me.jessyan.autosize.** { *; }
-dontwarn me.jessyan.autosize.**

# XXPermissions
-keep class com.hjq.permissions.** { *; }
-dontwarn com.hjq.permissions.**

# BaseRecyclerViewAdapterHelper
-keep class com.chad.library.** { *; }
-dontwarn com.chad.library.**

# SmartRefreshLayout
-keep class com.scwang.smart.refresh.** { *; }
-dontwarn com.scwang.smart.refresh.**

# Glide（若宿主未合并其 consumer rules，兜底保留）
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule { *; }
-keep class com.bumptech.glide.** { *; }
-dontwarn com.bumptech.glide.**

# Timber
-dontwarn org.jetbrains.annotations.**
