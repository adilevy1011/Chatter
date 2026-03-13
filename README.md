# Chatter – Real-time Chat Application in Java Swing

[![Java Version](https://img.shields.io/badge/Java-17+-blue)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)

Chatter is a desktop chat application built with **Java Swing** and **Firebase Realtime Database**, now with a **Python FastAPI backend**. Users can log in, send direct messages, or chat with all online users in real time, with online/offline tracking managed through the server.

---

## Features

### ✅ Current Features
- User login with username and password
- Automatic user registration if username doesn't exist
- Direct messaging between users
- Group chat with all online users
- Online/offline user tracking via FastAPI server
- Server API integration (`ServerAPI.java`) for all network communication

### ⚡ Planned Features
- File sharing
- Message notifications
- Heartbeat system to automatically remove inactive users
- Deployment to public server for fully cross-device use

---

## Technology Stack
- **Java 17+**
- **Swing** (GUI framework)
- **Firebase Realtime Database** (stores users, messages, conversations)
- **Python 3 + FastAPI** (backend server)
- **Gson** (JSON parsing in Java)

---

## Installation / Setup

### 1. Clone the repository
```bash
git clone https://github.com/adilevy1011/chatter
cd chatter
```
### 2. Backend setup (Python FastAPI server)
1. Install dependencies:
```bash
pip install fastapi uvicorn firebase-admin
```
2. Place your serviceAccountKey.json from Firebase in the server/ folder.
3. Start the server:
```bash
cd server
python -m uvicorn server:app --reload
```
Note: The server runs on the IP and port configured in ServerAPI.java (default in the code: http://192.168.56.1:8000).
Make sure to update SERVER_URL in ServerAPI.java if your computer’s IP changes or if you deploy the server to a different machine.
### 3. Firebase setup
1. Go to the Firebase Console and create a new project.
2. Add a new Android app (any package name is fine, e.g., com.example.chatter) to get the google-services.json file.
3. Place google-services.json in the project root (next to src/ and README.md).
4. Ensure your Realtime Database rules allow read/write for testing:
```bash
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```
### 4. Build and run the Java client
```bash
javac -d bin src/**/*.java
java -cp bin core.Launcher
```
**Important**: The Java client will only work if the server is running and SERVER_URL in ServerAPI.java points to the correct IP and port of the machine running the server.
- You **don’t have to hardcode `127.0.0.1`** anywhere — the client uses the `SERVER_URL` variable.  
- Users must adjust `SERVER_URL` if the server is on a different machine or IP.
---
## Server API Endpoints
| Endpoint             | Method | Description                                                |
| -------------------- | ------ | ---------------------------------------------------------- |
| `/sendMessage`       | POST   | Sends a public message to all users                        |
| `/sendDirectMessage` | POST   | Sends a direct message to a specific user                  |
| `/conversations`     | GET    | Retrieves all direct message conversations                 |
| `/conversation`      | GET    | Retrieve a specified direct message conversation           |
| `/messages`          | GET    | Retrieves all public messages                              |
| `/setOnline`         | POST   | Marks a user as online                                     |
| `/setOffline`        | POST   | Marks a user as offline                                    |
| `/newUser`           | POST   | Saves data for a new user
| `/login`             | POST   | Logs in an existing user or creates a new one if not found |
| `/onlineUsers`       | GET    | Retrieves a list of all online users                       |

---
## Usage 
1. Launch the application.
2. Log in with your username and password.
3. Send direct messages to a specific user or chat with everyone online.
4. Online users are automatically tracked and displayed.
---
## License
This project is licensed under the MIT License. See the LICENSE file for details.
