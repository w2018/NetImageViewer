# Glide
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl
-keep public class * implements com.bumptech.glide.module.GlideModule
# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.operit.netimageviewer.data.model.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Activity / Fragment - 保留所有 UI 组件类，确保 AndroidManifest.xml 中的声明不被混淆
-keep class com.operit.netimageviewer.ui.** { *; }

# Retrofit - 接口类必须完全保留（类名+方法名+泛型签名），否则反射解析失败
-keep,allowshrinking class com.operit.netimageviewer.data.remote.ApiService
-keepclassmembers class com.operit.netimageviewer.data.remote.ApiService {
    *** *(...);
}
-keep class com.operit.netimageviewer.data.remote.RetrofitClient { *; }

# Retrofit - 保留 Call 接口不被混淆（泛型签名用于 CallAdapter 匹配）
-keep,allowshrinking class retrofit2.Call
-keepclassmembers class retrofit2.Call {
    *** *(...);
}
-dontwarn retrofit2.**