# Chatter – Real-time Chat Application in Java Swing

[![Java Version](https://img.shields.io/badge/Java-17+-blue)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)

Chatter is a desktop chat application built with **Java Swing** and **Firebase Realtime Database**. Users can log in, send direct messages, or chat with all online users in real time.

---

## Features

### ✅ Current Features
- User login with username and password
- Direct messaging between users
- Group chat with all online users
- Online/offline user tracking

### ⚡ Planned Features
- File sharing
- Message notifications
- Backend integration for advanced storage

---

## Technology Stack
- **Java 17+**
- **Swing** (GUI framework)
- **Firebase Realtime Database** (serverless backend)
- Optional future backend: Spring Boot / Node.js

---

## Installation / Setup

1. Clone the repository:
```bash
git clone https://github.com/adilevy1011/chatter
cd chatter
```
### Firebase setup
2. Go to the Firebase Console and create a new project.
3. Once your project is created, navigate to Project Settings → General → Your apps and add a new Android app (you can use any package name like com.example.chatter).
4. Download the google-services.json file provided by Firebase.
5. Place the google-services.json file in the root of this project (next to src and README.md).
6. Make sure the Firebase Realtime Database is set up with rules that allow read/write for testing:
```bash
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```
7. After adding the file you can build and run the project as usual:
```bash
javac -d bin src/**/*.java
java -cp bin core.Launcher
```
## Usage 
1. Launch the application.
2. Log in with your username and password.
3. Send direct messages to a specific user or chat with everyone online.
4. Online users are automatically tracked and displayed.
## License
This project is licensed under the MIT License. See the LICENSE file for details.
