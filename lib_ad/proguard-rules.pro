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
-ignorewarnings
-dontwarn com.baidu.mobads.sdk.api.**
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class com.baidu.mobads.** { *; }
-keep class com.style.widget.** {*;}
-keep class com.component.** {*;}
-keep class com.baidu.ad.magic.flute.** {*;}
-keep class com.baidu.mobstat.forbes.** {*;}

-keep class android.support.v7.widget.RecyclerView {*;}
-keepnames class android.support.v7.widget.RecyclerView$* {
    public <fields>;
    public <methods>;
}
-keep class android.support.v7.widget.LinearLayoutManager {*;}
-keep class android.support.v7.widget.PagerSnapHelper {*;}
-keep class android.support.v4.view.ViewCompat {*;}
-keep class android.support.v4.util.LongSparseArray {*;}
-keep class android.support.v4.util.ArraySet {*;}
-keep class android.support.v4.view.accessibility.AccessibilityNodeInfoCompat {*;}


-keep class com.tencent.mm.opensdk.** {
    *;
}
-keep class com.tencent.wxop.** {
    *;
}
-keep class com.tencent.mm.sdk.** {
    *;
}
-keep class org.chromium.** {*;}
-keep class org.chromium.** { *; }
-keep class aegon.chrome.** { *; }
-keep class com.kwai.**{ *; }
-dontwarn com.kwai.**
-dontwarn com.kwad.**
-dontwarn com.ksad.**
-dontwarn aegon.chrome.**
-keep class com.yxcorp.kuaishou.addfp.android.Orange {*;}

##########################以下是ks建议要求把他们aar里面的混淆全部添加###################################

-keep class com.kwai.**{ *; }
-keep class com.ksad.**{ *; }
-keep class com.kwad.** { *;}
-keep class com.kuaishou.aegon.**{ *; }

-dontwarn com.kwai.**
-dontwarn com.kwad.**
-dontwarn com.ksad.**
-dontwarn aegon.chrome.**
-dontwarn com.kuaishou.aegon.**
-dontwarn com.tachikoma.core.**

#ijkplayer
-keep class org.chromium.** {*;}
-keep class org.chromium.** { *; }
-keep class aegon.chrome.** { *; }
-keeppackagenames com.badlogic.gdx.math
-keeppackagenames com.google.vrtoolkit.cardboard.sensors

-dontwarn okio.**
-dontwarn okhttp3.**
# 混淆一级即可
-keep class okhttp3.* {*;}
-keep class com.google.gson.* {*;}

#保留注解，如果不添加改行会导致我们的@Keep注解失效
-keepattributes *Annotation*
-keep class android.support.annotation.Keep
-keep @android.support.annotation.Keep class * {*;}

# 安全sdk
-dontwarn com.kuaishou.android.security.**
-keep class com.kuaishou.android.security.**{*;}
-keep class com.yxcorp.kuaishou.addfp.** { *;}

#直播相关混淆
-dontwarn io.netty.**
-dontwarn com.kuaishou.livestream.message.**
-dontwarn com.kuaishou.protobuf.**
-keep class com.google.protobuf.nano.** {*;}
-keep class com.kuaishou.livestream.message.nano.** {*;}
-keep class com.kuaishou.protobuf.livestream.nano.** {*;}
-keep class com.kuaishou.merchant.message.nano.** {*;}
-keep class com.kuaishou.protobuf.merchant.message.nano.** {*;}

#直播广告
-keep class okio.** {*;}
-keep class io.** {*;}
-keep class com.yxcorp.** {*;}
-keep class com.kuaishou.** {*;}
-keep class org.reactivestreams.** {*;}

-keep class com.kuaishou.livestream.** {*;}
-keep class com.kuaishou.security.kste.** {*;}
-keep class com.seclib.kste.** {*;}

# 安全sdk
-dontwarn com.kuaishou.android.security.**
-keep class com.kuaishou.dfp.**{*;}
-keep class com.kuaishou.dfp.KWEGIDDFP {*;}
-keep class com.kuaishou.dfp.ResponseDfpCallback {*;}
-keep class com.kuaishou.dfp.env.jni.Watermelon {*;}
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**
-keep class com.google.protobuf.** {*;}
-keep class com.kuaishou.dfp.env.Proxy.** {*;}
#new added for android 10
-keep class com.bun.miitmdid.core.** {*;}

#TK框架
-keep class com.tachikoma.core.manager.IProviderCollector {*;}
-keep class * implements com.tachikoma.core.manager.IProviderCollector { *;}

-keep class com.tkruntime.v8.**{*;}

-keep class com.kwad.proguard.annotations.DoNotStrip
-keep @com.kwad.proguard.annotations.DoNotStrip class * { *; }

-keepclassmembers,includedescriptorclasses class * { native <methods>; }

#风控sdk
-keep class com.kuaishou.weapon.**{ *; }

# oaid相关
-dontwarn com.kwad.sdk.oaid.OADIDSDKHelper**
-keep class * implements com.bun.miitmdid.interfaces.IIdentifierListener { *; }
-keep class com.bun.miitmdid.core.MdidSdkHelper{*;}
-keep public class com.kwad.sdk.crash.online.monitor.block.BlockEvent { *; }

-keep class com.kwad.sdk.glide.framesequence.FrameSequence { *; }
-keep class com.kwad.sdk.glide.framesequence.FrameSequenceDrawable { *; }


-keep class com.kwad.components.offline.api.** { *;}

-keep class * implements com.kwad.components.offline.api.IOfflineCompo { *; }

-keep class com.kwad.components.offline.api.core.annotation.DoNotStrip
-keep @com.kwad.components.offline.api.core.annotation.DoNotStrip class * { *; }

-keep class com.kwad.sdk.glide.framesequence.FrameSequence { *; }
-keep class com.kwad.sdk.glide.framesequence.FrameSequenceDrawable { *; }

-keep class com.kwad.components.offline.api.** { *;}

-keep class * implements com.kwad.components.offline.api.IOfflineCompo { *; }

-keep class com.kwad.components.offline.api.core.annotation.DoNotStrip
-keep @com.kwad.components.offline.api.core.annotation.DoNotStrip class * { *; }