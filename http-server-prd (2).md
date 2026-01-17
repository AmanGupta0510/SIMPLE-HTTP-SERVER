# HTTP Server Project - Product Requirements Document (PRD)

**Project ID**: Foundation-001  
**Phase**: Phase 1 (Weeks 2-3)  
**Duration**: 2 weeks (10-12 hours per week recommended)  
**Difficulty**: Medium  
**Primary Learning Outcome**: TCP/IP networking, concurrency, HTTP protocol, file I/O  

---

## Executive Summary

Build a functional HTTP/1.1 server from scratch that can serve static files, handle multiple concurrent connections, and respond to HTTP requests with proper status codes and headers. This project teaches networking fundamentals, socket programming, and concurrent request handling without using web frameworks.

**Key Constraint**: No frameworks allowed (no Express, Flask, etc.). Build using only language-native networking libraries and low-level socket APIs.

---

## Project Objectives

By the end of this project, you should understand:
1. How TCP/IP sockets work at the OS level
2. HTTP protocol structure (requests, responses, headers, status codes)
3. Multi-threaded or concurrent request handling
4. File I/O operations and MIME types
5. Error handling and edge cases in network programming

---

## Technical Requirements

### Core Features (Must Have)

#### 1. TCP Socket Server
- **Requirement**: Server listens on a specific port (default: 8080)
- **Implementation**:
  - Bind socket to localhost:8080
  - Listen for incoming connections
  - Accept and handle connections in a loop
  - Clean shutdown on Ctrl+C
- **Success Criteria**:
  - `curl http://localhost:8080/` connects without timeout
  - Server doesn't crash on connection attempts
  - Multiple connections can be accepted sequentially

#### 2. HTTP Request Parsing
- **Requirement**: Parse incoming HTTP requests correctly
- **Implementation**:
  - Read request from socket
  - Parse request line: `METHOD PATH HTTP_VERSION`
  - Parse headers: `Header-Name: value` pairs
  - Identify request body (if present)
  - Handle malformed requests gracefully
- **Supported Methods**: GET, POST, HEAD (minimum)
- **Success Criteria**:
  ```
  GET /index.html HTTP/1.1
  Host: localhost:8080
  User-Agent: curl/7.64.1
  ```
  Server correctly identifies: method=GET, path=/index.html, version=HTTP/1.1

#### 3. Static File Serving
- **Requirement**: Serve files from a designated directory
- **Implementation**:
  - Create a `public/` directory in project root
  - Map URL paths to filesystem paths
  - Read file contents and send as response
  - Set appropriate Content-Type headers
  - Handle file not found gracefully
- **Supported MIME Types** (minimum):
  - `text/plain` → .txt
  - `text/html` → .html
  - `application/json` → .json
  - `image/png` → .png
  - `image/jpeg` → .jpg
- **Success Criteria**:
  - `curl http://localhost:8080/test.txt` returns file contents
  - `curl -I http://localhost:8080/test.html` shows `Content-Type: text/html`
  - `curl http://localhost:8080/nonexistent.txt` returns 404 error

#### 4. HTTP Response Generation
- **Requirement**: Generate valid HTTP/1.1 responses
- **Implementation**:
  - Status line: `HTTP/1.1 STATUS_CODE STATUS_TEXT`
  - Response headers: `Header-Name: value`
  - Blank line separating headers and body
  - Response body (file contents or error message)
- **HTTP Status Codes to Support**:
  - `200 OK` - Successful request
  - `404 Not Found` - File doesn't exist
  - `400 Bad Request` - Malformed request
  - `500 Internal Server Error` - Server error
  - `405 Method Not Allowed` - Unsupported HTTP method
- **Required Response Headers**:
  - `Content-Type` - Correct MIME type
  - `Content-Length` - File size in bytes
  - `Server` - Server identification
  - `Connection` - Keep-alive or close
- **Success Criteria**:
  ```
  HTTP/1.1 200 OK
  Content-Type: text/html
  Content-Length: 1234
  Server: MyHTTPServer/1.0
  
  <html>...</html>
  ```

#### 5. Concurrent Connection Handling
- **Requirement**: Handle multiple simultaneous connections
- **Implementation**:
  - One thread/goroutine per connection (or async equivalent)
  - Each connection processed independently
  - No blocking on one connection affecting others
  - Proper resource cleanup
- **Success Criteria**:
  - Run `ab -n 10 -c 5 http://localhost:8080/` (Apache Bench)
  - Server handles 5 concurrent connections without hanging
  - All requests complete successfully

#### 6. Error Handling
- **Requirement**: Gracefully handle errors without crashing
- **Implementation**:
  - Malformed HTTP requests
  - File permission errors
  - File doesn't exist
  - Empty requests
  - Very large requests
  - Invalid MIME type requests
- **Success Criteria**:
  - Server never crashes on bad input
  - All error responses include appropriate status code
  - Error messages are user-friendly

### Advanced Features (Nice to Have)

#### 7. POST Request Support
- Accept POST requests with body
- Parse `Content-Type` and body content
- Store uploaded data (optional)
- Return appropriate response

#### 8. HEAD Request Support
- Return headers only, no body
- `Content-Length` must match GET response

#### 9. Directory Listing
- If path is directory, return list of files
- Format: HTML or JSON
- Clickable links for navigation

#### 10. Request Logging
- Log all requests to console or file
- Format: `timestamp method path status_code client_ip`
- Example: `2024-01-15 10:30:45 GET /index.html 200 127.0.0.1`

#### 11. Custom Error Pages
- HTML formatted error responses
- Include error code, message, suggestion

#### 12. Keep-Alive Connections
- Support HTTP/1.1 keep-alive
- Reuse TCP connection for multiple requests
- Proper timeout handling

---

## Project Structure

```
http-server/
├── src/
│   ├── Server.java          (or server.py, server.go)
│   ├── RequestParser.java    (parse HTTP requests)
│   ├── ResponseHandler.java  (generate HTTP responses)
│   └── FileServer.java       (serve files from disk)
├── public/                   (static files directory)
│   ├── index.html
│   ├── style.css
│   ├── test.txt
│   └── image.png
├── tests/
│   ├── ServerTest.java
│   ├── RequestParserTest.java
│   └── ResponseHandlerTest.java
├── Makefile                  (or build.sh for automation)
├── README.md                 (project documentation)
└── .gitignore               (git configuration)
```

---

## Implementation Steps (Recommended)

### Week 2 - Core Functionality

**Day 1-2: TCP Server & Request Parsing**
1. Create socket, bind to port 8080
2. Accept incoming connections
3. Read HTTP request from socket
4. Parse request line and headers
5. Test: `telnet localhost 8080` should connect

**Day 3-4: Response Generation & Static File Serving**
1. Generate valid HTTP response
2. Map URL paths to filesystem
3. Read file and serve with correct Content-Type
4. Test: `curl http://localhost:8080/test.html` should return file

**Day 5-6: Multi-threaded Handling**
1. Create thread/goroutine per connection
2. Each thread handles one request
3. Test: Multiple concurrent `curl` requests should work
4. Test: `ab -n 10 -c 5` should complete without hanging

**Day 7: Testing & Edge Cases**
1. Test all HTTP status codes (200, 404, 400, 500)
2. Test large files, small files, special characters in paths
3. Test malformed requests
4. Test concurrent connections

### Week 3 - Polish & Documentation

**Day 1-2: Advanced Features**
1. Implement POST support (optional)
2. Add request logging
3. Add directory listing (optional)

**Day 3-4: Testing & Benchmarking**
1. Write comprehensive unit tests
2. Run performance tests: `ab -n 100 -c 10`
3. Test edge cases thoroughly

**Day 5-6: Documentation & Code Review**
1. Write detailed README
2. Add architecture diagram
3. Document design decisions
4. Code cleanup and comments

**Day 7: Final Deployment & Repo Push**
1. Ensure code builds cleanly
2. All tests pass
3. Push to GitHub with clean commit history

---

## Learning Resources

### Video Tutorials
- **CodeCrafters HTTP Server Challenge**: https://app.codecrafters.io/challenges/http-server
  - Platform with incremental stages
  - Test cases provided
  - Estimated: 5-8 hours
- **CodeCrafters YouTube**: "Build Your Own HTTP Server from Scratch"
  - Live coding walkthrough
  - Duration: ~50 minutes

### Written Tutorials
- **"Writing an HTTP server from scratch"** by Bharat Chauhan
  - GitHub: https://github.com/bhch/crude-server
  - Full Python implementation walkthrough

### Reference Documentation
- **Mozilla HTTP Documentation**: https://developer.mozilla.org/en-US/docs/Web/HTTP
- **TCP/IP Socket Programming**:
  - Java: Official Java.net documentation
  - Python: Official socket module documentation
  - Go: Official net package documentation
- **HTTP/1.1 Specification**: RFC 7230-7235

### Networking Concepts
- **Video**: Neso Academy - Computer Networks playlist
  - TCP/IP concepts, protocols
  - Duration: 2-3 hours (optional for deeper understanding)

---

## Evaluation Criteria

### Code Quality (30%)
- [ ] Clean, readable code with appropriate comments
- [ ] Proper separation of concerns (parsing, serving, response handling)
- [ ] Follows language conventions and style guides
- [ ] No hardcoded values or magic numbers

### Functionality (50%)
- [ ] All core features working (must-haves)
- [ ] Handles concurrent connections properly
- [ ] Correct HTTP responses with proper headers
- [ ] Graceful error handling

### Testing (15%)
- [ ] Unit tests for request parsing
- [ ] Unit tests for response generation
- [ ] Integration tests for end-to-end functionality
- [ ] Edge case handling tested

### Documentation (5%)
- [ ] Clear README with usage instructions
- [ ] Architecture documented
- [ ] Design decisions explained
- [ ] Can be deployed by someone reading README

---

## Success Checklist

### Functional Verification
- [ ] Server starts without errors
- [ ] Server listens on port 8080
- [ ] Single request returns 200 OK with file contents
- [ ] Multiple concurrent requests work correctly
- [ ] 404 error for missing files
- [ ] 400 error for malformed requests
- [ ] Server doesn't crash on edge cases
- [ ] Server can be cleanly stopped with Ctrl+C

### Testing Verification
- [ ] Run `curl http://localhost:8080/index.html` → returns HTML
- [ ] Run `curl -I http://localhost:8080/test.txt` → shows Content-Type
- [ ] Run `curl http://localhost:8080/missing.txt` → returns 404
- [ ] Run `curl -X POST http://localhost:8080/` → returns 405 or handles POST
- [ ] Run `ab -n 100 -c 10 http://localhost:8080/` → no errors
- [ ] All unit tests pass

### Code Quality Verification
- [ ] Code compiles/runs without warnings
- [ ] No hardcoded paths (use configurable directory)
- [ ] Comments explain non-obvious logic
- [ ] Proper error messages in logs
- [ ] Clean Git history (logical commits)

### Documentation Verification
- [ ] README explains what the project does
- [ ] README has "How to Run" section
- [ ] README has "Design Decisions" section
- [ ] Architecture diagram included
- [ ] Test results documented

---

## Common Challenges & Solutions

### Challenge 1: "How do I read from a socket?"
**Solution**: Use language-native APIs:
- Java: `Socket` class, `InputStream`
- Python: `socket` module, `socket.recv()`
- Go: `net.Listener`, `conn.Read()`

### Challenge 2: "How do I parse HTTP requests?"
**Solution**: 
- Read until you find `\r\n\r\n` (end of headers)
- Split by `\r\n` to get lines
- First line: `METHOD PATH VERSION`
- Other lines: `Header-Name: value`

### Challenge 3: "How do I handle multiple connections?"
**Solution**:
- Java: `new Thread()` for each connection
- Python: `threading.Thread()` for each connection
- Go: `go` keyword for goroutines

### Challenge 4: "The file I'm serving has wrong Content-Type"
**Solution**:
- Map file extensions to MIME types
- Use common extensions: `.html` → `text/html`, `.txt` → `text/plain`
- Default to `application/octet-stream` for unknown

### Challenge 5: "My server crashes on concurrent requests"
**Solution**:
- Ensure each connection has its own thread/goroutine
- Use thread-safe file operations
- Close connections properly after use

---

## Performance Expectations

**Baseline Performance**:
- Single request latency: < 10ms
- Concurrent connections (10): < 100ms total for all
- File serving speed: Limited by disk I/O (~1MB/s typical SSD)

**Benchmarking Command**:
```bash
# Install Apache Bench (if not installed)
# Ubuntu/Debian: sudo apt-get install apache2-utils
# macOS: brew install httpd

# Test with 100 requests, 10 concurrent
ab -n 100 -c 10 http://localhost:8080/index.html

# View results: requests/sec, failed requests, response times
```

---

## Deployment & Sharing

### Before Final Push
1. Ensure all tests pass
2. Clean up temporary files
3. Update README with final information
4. Make sure no credentials are in code
5. Verify clean commit history

### GitHub Push
```bash
git add .
git commit -m "HTTP Server implementation - Week 2"
git push origin main
```

### Sharing for Code Review
- Share GitHub link with mentor/peer
- Include architecture diagram in README
- Highlight any advanced features implemented
- Be ready to explain design decisions

---

## Interview Talking Points

When discussing this project in interviews:

**What it teaches**:
- "This project taught me how HTTP works at the protocol level"
- "I learned concurrent programming and thread management"
- "I understand socket programming and network I/O"

**Design decisions to mention**:
- "I used one thread per connection to handle concurrency"
- "I parsed HTTP headers by splitting on \\r\\n"
- "I mapped file extensions to MIME types for correct Content-Type"

**Challenges overcome**:
- "I had to handle edge cases like concurrent file access"
- "I implemented proper error handling for malformed requests"
- "I optimized for performance using keep-alive connections"

**Real-world applicability**:
- "This is similar to how real web servers like Nginx handle requests"
- "The concepts apply to any network protocol implementation"
- "Understanding this helps with microservices and API servers"

---

## Presentation Preparation

For the capstone presentation (Week 22), be ready to discuss:

### 5-Minute Overview
"I built an HTTP server that accepts connections on port 8080, parses HTTP requests, serves static files, and handles multiple concurrent connections using threads. It supports GET, POST, and HEAD methods with proper HTTP status codes."

### 15-Minute Demo
1. Show project running: `./run.sh` or `java Server`
2. Make sample requests: `curl`, browser, Apache Bench
3. Show test results passing
4. Walk through code structure (parsing → handling → response)

### 25-Minute Deep Dive
1. Explain TCP/IP socket programming choices
2. Show how concurrent connections are handled
3. Walk through HTTP request parsing logic
4. Explain MIME type mapping
5. Show error handling strategy
6. Performance metrics and benchmarks

### 10-Minute Q&A
- "What would you change at scale?" → Connection pooling, async I/O
- "How would you add HTTPS?" → SSL/TLS layer above socket
- "What about keep-alive connections?" → I implemented it, here's how...
- "How does it compare to real servers?" → It's simplified but follows same principles

---

## Success Stories

**What students typically say after completing this project**:

- "I finally understand how networks actually work"
- "I feel confident reading web server code now"
- "This made me appreciate frameworks – but I know what's happening under the hood"
- "I can explain HTTP to someone not technical"
- "This was harder than I thought but so satisfying when it worked"

---

## Next Steps After Completion

1. **Code Review**: Get feedback from mentor on design/implementation
2. **Optimization**: Try improving performance or adding advanced features
3. **Portfolio**: Add to GitHub portfolio with detailed README
4. **Interview Prep**: Use this project to discuss in technical interviews
5. **Extension**: Consider adding HTTPS, caching, or compression

---

## FAQ

**Q: Can I use a web framework?**  
A: No. The point is to learn how HTTP works under the hood. Frameworks abstract this away.

**Q: How long should this take?**  
A: 10-12 hours per week for 2 weeks. Most of Week 2 on core functionality, Week 3 on polish.

**Q: Do I need to implement all features?**  
A: Core features (1-6) are required. Advanced features (7-12) are optional but impressive.

**Q: What language should I use?**  
A: Java, Python, or Go are recommended. Any language with socket support works.

**Q: Can I look at solutions online?**  
A: Yes, but try to understand concepts first. Looking at code should help you learn, not replace learning.

**Q: How do I know if my server is correct?**  
A: Use curl, Apache Bench, or browser testing. Compare behavior with real servers.

**Q: What's the most common mistake?**  
A: Not handling concurrent connections properly, or not parsing HTTP headers correctly.

---

## Conclusion

This project is your gateway to understanding network programming. By building a real HTTP server, you'll understand the protocols, networking concepts, and system design principles used in production systems. The code you write here is a foundation for understanding everything from microservices to cloud infrastructure.

Good luck! 🚀

---

**Project Created**: Week 2-3 of 6-month curriculum  
**Estimated Completion**: 20-24 hours of focused work  
**Difficulty Level**: Medium  
**Portfolio Value**: High ⭐⭐⭐⭐⭐