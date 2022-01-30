##########################################################################################
# Kotlin
##########################################################################################
-dontnote kotlin.**

##########################################################################################
# Dagger 2
##########################################################################################
-dontwarn com.google.errorprone.annotations.**

##########################################################################################
# Retrofit 2
# http://square.github.io/retrofit/
##########################################################################################
-dontwarn retrofit2.**

# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep annotation default values (e.g., retrofit2.http.Field.encoded).
-keepattributes AnnotationDefault

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

##########################################################################################
# Okhttp3
# https://square.github.io/okhttp/r8_proguard/
##########################################################################################
-dontwarn javax.annotation.**
-dontwarn okhttp3.**
-dontnote okhttp3.**

##########################################################################################
# Okio
# https://square.github.io/okio/#r8-proguard
##########################################################################################
-dontwarn javax.annotation.**
-dontwarn okio.**

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

##########################################################################################
# Moshi
# https://github.com/square/moshi#proguard
##########################################################################################

##########################################################################################
# Glide
# https://github.com/bumptech/glide#proguard
##########################################################################################
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

##########################################################################################
# Others
##########################################################################################
-dontnote android.net.http.**
-dontnote org.apache.http.**
