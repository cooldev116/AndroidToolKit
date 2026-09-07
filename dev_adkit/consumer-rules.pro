# ============================================================
# Dev AdKit — consumer ProGuard / R8
# 随 AAR 打包，宿主开启 minify 时自动合并。
# 含本库 API + 穿山甲聚合及已接入 ADN（GDT / 百度 / 快手）官方 keep。
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
# 宿主调用 AdKit、继承开屏 Activity、实现 ISplashStyle / AdFlowCallback、Gson 配置模型等
-keep class com.adwant.kit.** { *; }
-keep interface com.adwant.kit.** { *; }
-keep enum com.adwant.kit.** { *; }

# ViewBinding
-keepclassmembers class * implements androidx.viewbinding.ViewBinding {
    public static *** inflate(...);
    public static *** bind(...);
}

# Retrofit（AdKitService）/ Gson
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

# Activity 布局点击方法（部分 ADN / 兼容旧写法）
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

# 枚举
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# -------------------- 穿山甲 / 聚合 Mediation --------------------
-keep class bykvm*.** { *; }
-keep class com.bytedance.msdk.adapter.** { public *; }
-keep class com.bytedance.msdk.api.** { public *; }
-keep class com.bytedance.msdk.base.TTBaseAd { *; }
-keep class com.bytedance.msdk.adapter.TTAbsAdLoaderAdapter {
    public *;
    protected <fields>;
}
-keep class com.bytedance.sdk.openadsdk.** { *; }
-keep public interface com.bytedance.sdk.openadsdk.downloadnew.** { *; }
-keep class com.bytedance.pangle.** { *; }
-keep class com.bytedance.frameworks.** { *; }
-keep class com.bytedance.embedapplog.** { *; }
-keep class com.bytedance.embed_dr.** { *; }
-keep class com.bykv.vk.** { *; }
-keep class ms.bd.c.Pgl.** { *; }
-keep class com.bytedance.mobsec.metasec.ml.** { *; }
-keep class com.ss.android.** { *; }
-dontwarn com.bytedance.**
-dontwarn bykvm.**
-dontwarn com.bykv.**

# -------------------- 优量汇 GDT --------------------
-keep class com.qq.e.** { *; }
-keep class com.tencent.**.gdt.** { *; }
-keep public interface com.qq.e.ads.interstitial2.** { *; }
-keep public interface com.qq.e.ads.interstitial3.** { *; }
-keep public interface com.qq.e.ads.rewardvideo.** { *; }
-keep public interface com.qq.e.ads.rewardvideo2.** { *; }
-keep public interface com.qq.e.ads.banner2.** { *; }
-keep public interface com.qq.e.comm.adevent.** { *; }
-dontwarn com.qq.e.**

# -------------------- 百度联盟 --------------------
-dontwarn com.baidu.mobads.sdk.api.**
-keep class com.baidu.mobads.** { *; }
-keep class com.style.widget.** { *; }
-keep class com.component.** { *; }
-keep class com.baidu.ad.magic.flute.** { *; }
-keep class com.baidu.mobstat.forbes.** { *; }

# -------------------- 快手 KS --------------------
-keep class org.chromium.** { *; }
-keep class aegon.chrome.** { *; }
-keep class com.kwai.** { *; }
-keep class com.ksad.** { *; }
-keep class com.kwad.** { *; }
-keep class com.kuaishou.aegon.** { *; }
-keep class com.yxcorp.kuaishou.addfp.android.Orange { *; }
-keep class com.yxcorp.kuaishou.addfp.** { *; }
-keep class com.kuaishou.android.security.** { *; }
-keep class com.kuaishou.dfp.** { *; }
-keep class com.kuaishou.weapon.** { *; }
-keep class com.kwad.components.offline.api.** { *; }
-keep class * implements com.kwad.components.offline.api.IOfflineCompo { *; }
-keep class com.kwad.proguard.annotations.DoNotStrip
-keep @com.kwad.proguard.annotations.DoNotStrip class * { *; }
-keep class com.kwad.components.offline.api.core.annotation.DoNotStrip
-keep @com.kwad.components.offline.api.core.annotation.DoNotStrip class * { *; }
-keepclassmembers,includedescriptorclasses class * {
    native <methods>;
}
-dontwarn com.kwai.**
-dontwarn com.kwad.**
-dontwarn com.ksad.**
-dontwarn aegon.chrome.**
-dontwarn com.kuaishou.aegon.**
-dontwarn com.tachikoma.core.**
-dontwarn com.kuaishou.android.security.**

# -------------------- OAID（聚合 / ADN 可能用到） --------------------
-dontwarn com.bun.**
-keep class com.bun.** { *; }
-keep class com.bun.miitmdid.core.** { *; }
-keep class * implements com.bun.miitmdid.interfaces.IIdentifierListener { *; }
-keep class com.asus.msa.SupplementaryDID.** { *; }
-keep class com.asus.msa.sdid.** { *; }
-keep class com.huawei.hms.ads.identifier.** { *; }
-keep class com.samsung.android.deviceidservice.** { *; }
-keep class com.zui.opendeviceidlibrary.** { *; }
-keep public class com.netease.nis.sdkwrapper.Utils {
    public <methods>;
}

# -------------------- Sigmob（当前未接入，预留；接入时取消注释依赖即可） --------------------
# -dontwarn com.sigmob.**
# -keep class com.sigmob.** { *; }
# -dontwarn android.support.v4.**
# -keep class android.support.v4.** { *; }

# 聚合 / ADN 内部可能引用缺失类，避免宿主打包被 warning 打断
-dontwarn sun.misc.Unsafe
-keep class sun.misc.Unsafe { *; }
