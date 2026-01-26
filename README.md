<h1>Simple HTTP Server From Scratch In Java</h1>
<h4>A lightweight,HTTP/1.1 server built entirely from java native library <b>ServerSocket</b></h4>
<br>
<br>
<br>

<Features>
<ul>
<li>Full HTTP/1.1 parsing (GET,POST,Header)</li>
<li>Static File Serving(public/folder)</li>
<li>Multi-Threaded(handle concurrent request)
<li>Custom routing(/login , /login.html -> public/login.html...)</li>
<li>HTML Templating ({{username}} -> dynamic data as provided in the body during POST method)</li>
</ul>
<br>
<br>
<br>
<h1>Quick Start</h1>
# 1. Clone & compile
git clone <repository_name>
cd Simple-Http-Server
javac -d . src\*.java

# 2. Run server
java Server

# 3. Test in browser
http://localhost:8080/

