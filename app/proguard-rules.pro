# ========================================
# Glide
# ========================================
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl
-keep public class * implements com.bumptech.glide.module.GlideModule

# ========================================
# Room - 数据库实体（Room 通过反射读写字段）
# ========================================
-keep class * extends androidx.room.RoomDatabase
-keep class com.operit.netimageviewer.data.local.** { *; }
-dontwarn androidx.room.paging.**

# ========================================
# Gson - 数据模型（Gson 通过反射读写字段）
# ========================================
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.operit.netimageviewer.data.model.** { *; }
-keep class com.operit.netimageviewer.data.local.** { *; }

# ========================================
# Retrofit + OkHttp
# ========================================
-keepattributes Signature, *Annotation*, EnclosingMethod, InnerClasses
-keep,allowshrinking class com.operit.netimageviewer.data.remote.ApiService
-keepclassmembers class com.operit.netimageviewer.data.remote.ApiService {
    *** *(...);
}
-keep class com.operit.netimageviewer.data.remote.RetrofitClient { *; }
-keep,allowshrinking class retrofit2.Call
-keepclassmembers class retrofit2.Call {
    *** *(...);
}
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# ========================================
# UI 组件 - Activity / Fragment / Adapter / ViewHolder
# ========================================
-keep class com.operit.netimageviewer.ui.** { *; }

# ========================================
# PhotoView
# ========================================
-keep class uk.co.senab.photoview.** { *; }
-keep class com.github.chrisbanes.photoview.** { *; }

# ========================================
# 通用 - 保留泛型、注解、枚举、Parcelable
# ========================================
-keepattributes Exceptions, InnerClasses, PermittedSubclasses
-keepclassmembers enum * { *; }
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keep class * implements java.io.Serializable { *; }

# ========================================
# R8 全模式 - 避免 shrink 误删
# ========================================
-keep,allowshrinking class *