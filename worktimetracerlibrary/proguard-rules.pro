# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\PF\adt-bundle-windows-x86_64-20131030\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#OrmLite

-keep class pl.rafalmag.worktimetracerlibrary.db.** { *; }

# OrmLite uses reflection
-keep class com.j256.**
-keepclassmembers class com.j256.** { *; }
-keep enum com.j256.**
-keepclassmembers enum com.j256.** { *; }
-keep interface com.j256.**
-keepclassmembers interface com.j256.** { *; }
-keepattributes Signature

-keepclassmembers class * {
  public <init>(android.content.Context);
}

-keepattributes *Annotation*

# Keep the helper class and its constructor
-keep class * extends com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
-keepclassmembers class * extends com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper {
  public <init>(android.content.Context);
}

# Keep all model classes that are used by OrmLite
# Also keep their field names and the constructor
#-keep @com.j256.ormlite.table.DatabaseTable class * {
#    @com.j256.ormlite.field.DatabaseField <fields>;
#    @com.j256.ormlite.field.ForeignCollectionField <fields>;
#    # Add the ormlite field annotations that your model uses here
#    <init>();
#}

#if you're trying to serialize / deserialize Joda's DateTime objects via ORMLite, you probably need this as well:
-keepclassmembers class **DateTime {
    <init>(long);
    long getMillis();
}