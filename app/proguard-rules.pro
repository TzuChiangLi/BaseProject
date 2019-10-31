# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

#打开混淆日志
-verbose
# 不混淆泛型
-keepattributes Signature

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#-injars      bin/classes
#-injars      bin/resources.ap_
#-injars      libs
#-outjars     bin/application.apk
-libraryjars D:/Android/android-sdk-windows/platforms/android-28/android.jar

-android
#-dontpreverify
-repackageclasses ''
#-allowaccessmodification
#-optimizations !code/simplification/arithmetic
-dontoptimize
-keepattributes *Annotation*


-keep class !com.ftrend.** { *; }
-keep interface !com.ftrend.** { *; }
-keep class com.ftrend.zgp.utils.http.** { *; }
-keep class com.ftrend.zgp.model.** { *; }
-keep class com.ftrend.zgp.view.** { *; }
-keep class com.ftrend.zgp.presenter.** { *; }
-keep class com.ftrend.zgp.base.** { *; }
-keep interface com.ftrend.zgp.base.** { *; }
-keep interface com.ftrend.zgp.api.** { *; }
-dontwarn !com.ftrend.**

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.content.Context {
   public void *(android.view.View);
   public void *(android.view.MenuItem);
}

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

##### DBFlow #####
-keep class * extends com.raizlabs.android.dbflow.config.DatabaseHolder { *; }

##### Butterknife #####
-keep @interface butterknife.*

-keepclasseswithmembers class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembers class * {
    @butterknife.* <methods>;
}

-keepclasseswithmembers class * {
    @butterknife.On* <methods>;
}

-keep class **$$ViewInjector {
    public static void inject(...);
    public static void reset(...);
}

-keep class **$$ViewBinder {
    public static void bind(...);
    public static void unbind(...);
}

-if   class **$$ViewBinder
-keep class <1>

-keep class **_ViewBinding {
    <init>(<1>, android.view.View);
}

-if   class **_ViewBinding
-keep class <1>

##### RxJava #####
-keep class io.reactivex.** { *; }
-keep interface io.reactivex.** { *; }
-dontwarn io.reactivex.**

##### Retrofit #####
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-dontwarn retrofit2.**

-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

-keep class okio.** { *; }
-keep interface okio.** { *; }
-dontwarn okio.**

##### SoulPermission #####
-keep class com.qw.** { *; }
-keep interface com.qw.** { *; }
-dontwarn com.qw.**
