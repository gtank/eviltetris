eviltetris-client
=================

Installation instructions:

This app was built to specifications of Android 2.3, but should theoretically work with any version of android.

Mobile Device:
	Copy eviltetris.apk to device's internal memory or sd card.
	Using a file manager on the device open eviltetris.apk.
	Android's package manager will install eviltetris to the device.
	
	Alternatives:
		Using the Android Debug Bridge (adb) the apk can be pushed and installed to the device over usb, there are many guides on this, not ennumerated here.

Emulator:
	Using the Android Debug Bridge run the following command to push the apk to install on a running emulator.
		.adb -e install eviltetris.apk
	
	Alternatives:
		Most emulators are run from IDE's, load the source code into an IDE that has been setup with the Android Developer Tools and SDK and refer to the IDE's documentation for running.

To see what activity the app is displaying:
	Packet capture software and viewing the logcat from adb should display any activity.


