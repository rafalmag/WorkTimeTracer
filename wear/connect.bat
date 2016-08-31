cd C:\PF\adt-bundle-windows-x86_64-20131030\sdk\platform-tools
adb forward tcp:4444 localabstract:/adb-hub
adb connect 127.0.0.1:4444
adb devices
