eviltetris server
=================

eviltetris server is written in python 2, you will need python 2 installed on the machine you wish to run as the server.

Running the server:
From the command line in UNIX:
	python evilserver/evilserver.py
Or run evilserver.py an IDE.

The source code of the app is hardcoded to a server for testing, if you wish to use your own server you will need to change the IP address:
	/eviltetris/src/android/game/score
		line 46
		change the URL of the 'logServer' variable,
		ensure that the port is 5000

Troubleshooting
---------------
Ensure that the evilserver can listen on port 5000 and accept incoming requests through the firewall.


