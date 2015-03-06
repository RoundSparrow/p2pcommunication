## P2P Communication
This project is a 'proof-of-concept' on establishing a P2P connection between two or more android-devices, and make them communicate using Multicast (UDP). This is accomplished by utilizing the [WiFi P2P API](http://developer.android.com/guide/topics/connectivity/wifip2p.html), also known as WiFi Direct.

### Prerequisites
 - [Android Studio Bundle](http://developer.android.com/sdk/index.html#)
 - [Java Development Kit](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
 - [Git](http://git-scm.com/downloads)

### Getting started
If you encounter problems, see "troubleshooting" section below.
- Install Java Development Kit
- Install Android Studio Bundle
- Set up Android SDK with [Android Support Library](https://developer.android.com/tools/support-library/setup.html)
- Install Git
- Check out the project from GitHub repository
- Import project in Android Studio

### Troubleshooting
- [Android Studio cannot find git.exe (Create process error=2)](https://github.com/bouvet-bergen/p2pcommunication/wiki/Set-git-executable-path)
- [Android Studio cannot find JDK](https://github.com/bouvet-bergen/p2pcommunication/wiki/Set-JAVA_HOME-environment-variable)
- Android Virtual Devices has limited support for WiFi Direct so testing the app on a physical device is recommended.
- WiFi Direct is buggy on devices running older Android-versions than 4.2 Jelly Bean. [Issue tracker](https://code.google.com/p/android/issues/detail?id=43004)
- ["Message not multicasted" when trying to send multicast messages]() (Log: networkInterface == null)

### FAQ
- Minimum SDK version: 16
- Target SDK version: 21



