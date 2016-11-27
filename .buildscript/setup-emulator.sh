#!/usr/bin/env bash

# Emulator
echo y | android update sdk -u -a -t android-19
echo y | android update sdk -u -a -t sys-img-armeabi-v7a-android-19

# Setup emulator
echo no | android create avd --force -n test -t android-19 --abi armeabi-v7a --sdcard 200M
emulator -avd test -no-skin -no-audio -no-window &
android-wait-for-emulator
sleep 30

# Turn off animations
adb shell settings put global window_animation_scale 0 &
adb shell settings put global transition_animation_scale 0 &
adb shell settings put global animator_duration_scale 0 &

# Fix for devices API < 19
adb shell setprop dalvik.vm.dexopt-flags v=n,o=v
adb shell stop installd
adb shell start installd

# Wake up
adb shell input keyevent 82 &
