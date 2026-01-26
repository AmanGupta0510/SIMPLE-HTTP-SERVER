<h1>Simple HTTP Server From Scratch In Java</h1>
<h4>A lightweight,HTTP/1.1 server built entirely from java native library <b>ServerSocket</b></h4>
<br>
<br>
<br>

<h1>Features</h1>
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
<br>
<h3>git clone <repository_name></h3>
<br>
<h3>cd Simple-Http-Server</h3>
<br>
<h3>javac -d . src\*.java</h3>

# 2. Run server
<h3>java Server</h3>

# 3. Test in browser
http://localhost:8080/


<h2>Project Structure</h2>
Simple-Http-Server/<br>
├── src/<br>
│   └── FileServer.java      # Handling file as per method(GET,POST) <br>
│   └── HttpReq.java         # HttpReq class(Getter,Setter,Constructor)<br>
│   └── HttpResponse.java    # HttpResponse class(Getter,Setter,Constructor)<br>
│   └── HttpServerApp.java   # Server Interface (Start,Stop)<br>
│   └── requestHandler.java  # Interface (ResPonse Generator)<br>
│   └── RequestParser.java   # Handling & parsing request(Header,Body)<br>
│   └── ResponseHandler.java # Handling Response (creating correct response)<br>
│   └──Server.java            # Main server<br>
│   └──serverHandler.java      # Handling Concurrent request and server<br>
├── public/                  # Static files<br>
│   ├── index.html           # home Page<br>
│   ├── login.html           # login Page (POST form)<br>
│   ├── about.html           # about Page({{username}} template)<br>
│   ├── error.html           # error Page<br>
│   ├── style-index.css  <br>     
│   ├── style-login.css<br>
│   └── bg.jpg <br>
│
└── README.md <br>


