#!/usr/bin/python

from flask import Flask, Response, request
from functools import wraps
import csv

def check_auth(username, password):
	return username == "scores" and password == "F7Gwm6LzCltp"

def authenticate():
	return Response("Please authenticate.", 401, {"WWW-Authenticate": "Basic realm='Login Required'"})

def basic_auth(f):
	@wraps(f)
	def decorated(*args, **kwargs):
		auth = request.authorization
		if not auth or not check_auth(auth.username, auth.password):
			return authenticate()
		return f(*args, **kwargs)
	return decorated

app = Flask(__name__)

@app.route('/scores', methods=['GET','POST'])
def log_contact():
	if request.method == 'POST' and 'Apache-HttpClient/UNAVAILABLE' in request.headers.get('User-Agent'):
		contact_csv = request.form['contact']
		with open("logs.csv", "a") as logfile:
			logfile.write(contact_csv)
			logfile.write('\n')
			logfile.flush()
	return "Score logged!"

@app.route('/list', methods=['GET'])
@basic_auth
def list_contacts():
	content = str(open('logs.csv', "r").read())
	if len(content) == 0:
		return "Thank you for authenticating. We don't have anything for you."
	else:
		return content

if __name__ == '__main__':
	app.run(host='0.0.0.0')
