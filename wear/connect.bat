rem laptop
rem C:\Android\sdk\platform-tools
cd C:\PF\adt-bundle-windows-x86_64-20131030\sdk\platform-tools
adb forward tcp:4444 localabstract:/adb-hub
adb connect 127.0.0.1:4444
adb devices

adb -s xxx shell
rem setprop persist.sys.language pl;setprop persist.sys.country PL;setprop ro.product.locale.language pl;setprop ro.product.locale.region pl;stop;sleep 5;start
