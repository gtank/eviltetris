eviltetris server
=================

eviltetris server is written in Python 2.6, you will need Python 2.6+ (not Python 3) & pip installed on the machine you wish to run as the server.

Running the server:
From the command line in UNIX:
	cd evilserver
	python evilserver.py
	pip install -r requirements

The source code of the app is hardcoded to a server for testing, if you wish to use your own server you will need to change the IP address:
	/eviltetris/src/android/game/score/ScoreManager.java
		line 46
		change the URL of the 'logServer' variable,
		ensure that the port is 5000

The output can be read raw at /list, or if ran through curl the contacts can viewed much more clearly.


Troubleshooting
---------------
Ensure that the evilserver can listen on port 5000 and accept incoming requests through the firewall.


