# Add project specific ProGuard rules here.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Keep Room classes
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Keep ML Kit
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

# Keep math parser
-keep class org.mariuszgromada.math.** { *; }
-dontwarn org.mariuszgromada.math.**

# Keep iText PDF
-keep class com.itextpdf.** { *; }
-dontwarn com.itextpdf.**
-dontwarn org.bouncycastle.**
-dontwarn org.slf4j.**

# Compose
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }
