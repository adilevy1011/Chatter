# Chatter – Real-time Chat Application in Java Swing

[![Java Version](https://img.shields.io/badge/Java-25+-blue)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)

Chatter is a desktop chat application built with **Java Swing** and **Firebase Realtime Database**, now with a **Python FastAPI backend**. Users can log in, send direct messages, or chat with all online users in real time, with online/offline tracking managed through the server.

---

## Features

### ✅ Current Features
- User login with username and password
- Automatic user registration if username doesn't exist
- Direct messaging between users
- Group chat with all online users
- Online/offline user tracking via FastAPI server with heartbeat system (users marked offline immediately on app close, or after 30 seconds of inactivity)
- Unread messages indicator
- Server API integration (`ServerAPI.java`) for all network communication

### ⚡ Planned Features
- File sharing
- Message notifications
- Public server deployment for internet-wide access

---

## Technology Stack
- **Java 25**
- **Swing** (GUI framework)
- **Firebase Realtime Database** (stores users, messages, conversations)
- **Python 3 + FastAPI** (backend server)
- **Gson** (JSON parsing in Java)
- **Gradle** (build tool)

---

## Prerequisites
- **Java 25** (Temurin recommended)
- **Python 3.8+**
- **Gradle 9.4+**
- **Firebase project** with Realtime Database enabled

---

### 1. Clone the repository
```bash
git clone https://github.com/adilevy1011/Chatter
cd Chatter
```
### 2. Backend setup (Python FastAPI server)
1. Install dependencies:
```bash
pip install fastapi uvicorn firebase-admin
```
2. Obtain your Firebase service account key:
   - Go to Firebase Console > Project Settings > Service Accounts
   - Generate a new private key (JSON file)
   - Rename it to `serviceAccountKey.json` and place it in the `server/` folder
   - **Important**: This file is gitignored and not included in the repository for security
3. Start the server (accessible on your local network):
```bash
cd server
python -m uvicorn server:app --host 0.0.0.0 --port 8000 --reload
```
Note: The server will be accessible at `http://<your-ip>:8000`. The Java client automatically detects the server URL via environment variables or system properties.
### 3. Firebase setup
1. Go to the Firebase Console and create a new project.
2. Enable the Realtime Database.
3. Set up authentication if needed (though the app handles its own user management).
4. Configure your Realtime Database rules for security (replace the default open rules):
```json
{
  "rules": {
    "users": {
      ".read": "auth != null",
      ".write": "auth != null",
      "$userId": {
        ".read": "auth != null && auth.uid == $userId",
        ".write": "auth != null && auth.uid == $userId"
      }
    },
    "messages": {
      ".read": "auth != null",
      ".write": "auth != null"
    },
    "conversations": {
      ".read": "auth != null",
      ".write": "auth != null",
      "$conversationId": {
        ".read": "auth != null",
        ".write": "auth != null"
      }
    }
  }
}
```
Note: The app uses Firebase Admin SDK on the server side, so authentication is handled server-side. Adjust rules based on your security requirements.
### 4. Build and run the Java client
1. Ensure you have Java 25 and Gradle installed.
2. Build the project:
```bash
gradle build
```
3. Run the application:
```bash
gradle run
```
**Configuring the Server URL:**
The client automatically detects the server URL in this order:
1. System property: `-Dserver.url=http://<your-ip>:8000`
2. Environment variable: `SERVER_URL=http://<your-ip>:8000`
3. Default: `http://localhost:8000`

For LAN usage, set the environment variable:
```bash
# Windows PowerShell
$env:SERVER_URL = "http://<your-local-ip>:8000"
gradle run

# Or with system property
gradle run -Dorg.gradle.jvmargs="-Dserver.url=http://<your-local-ip>:8000"
```

**Important**: The Java client requires the FastAPI server to be running and accessible. For GitHub sharing, users can run the server on their own machine and configure the URL accordingly.
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
| `/heartbeat`         | POST   | Updates user's last seen timestamp for online tracking     |
| `/setOffline`        | POST   | Marks a user as offline                                    |
| `/newUser`           | POST   | Saves data for a new user                                  |
| `/login`             | POST   | Logs in an existing user or creates a new one if not found |
| `/onlineUsers`       | GET    | Retrieves a list of all online users (active within 30s)   |

---
## Usage 
1. Start the FastAPI server as described above.
2. Configure the server URL if needed (see Build and Run section).
3. Launch the Java application with `gradle run`.
4. Log in with your username and password.
5. Send direct messages to a specific user or chat with everyone online.
6. Unread messages are automatically tracked and displayed in each conversation.
7. Online users are automatically tracked and displayed.
8. **To close the app properly**: Click the window's X button (not the VSCode stop button). This ensures the user is marked offline in Firebase.

**For LAN/Shared Network Usage:**
- Run the server with `--host 0.0.0.0`
- Set `SERVER_URL` to the server's IP address (e.g., `http://192.168.1.100:8000`)
- Multiple clients on the same network can connect and chat together
---
## License
This project is licensed under the MIT License. See the LICENSE file for details.
