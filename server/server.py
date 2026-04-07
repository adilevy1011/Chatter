import firebase_admin
from firebase_admin import credentials, db
from fastapi import FastAPI
import time
from pydantic import BaseModel, Field
from fastapi.middleware.cors import CORSMiddleware
import os
from typing import List, Dict
app = FastAPI()


# Allow your Java app to access server (CORS)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # in production, restrict this
    allow_methods=["*"],
    allow_headers=["*"],
)

# Initialize Firebase
cred = credentials.Certificate("serviceAccountKey.json")
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://chatter1-74374-default-rtdb.firebaseio.com/'
})

class Message(BaseModel):
    username: str
    to: str = None  # for direct messages
    readForUser1: bool = False
    readForUser2: bool = False
    text: str
    timestamp: float = Field(default_factory=time.time)
    messageID: str = Field(default_factory=lambda: os.urandom(16).hex())
class Conversation(BaseModel):
    user1: str
    user2: str
    messages: List[Dict]  # or create a Message model

@app.get("/messages")
def get_messages():
    ref = db.reference("messages")
    messages = ref.get()
    if messages:
        return list(messages.values())
    return []

@app.post("/sendMessage")
def send_message(msg: Message):
    ref = db.reference("messages")
    ref.push({
        "username": msg.username,
        "text": msg.text,
        "timestamp": msg.timestamp,
        "messageID": msg.messageID,   
                   
    })
    return {"status": "sent"}

@app.post("/sendDirectMessage")
def send_direct_message(msg: Message, sender: str, conversationID: str):

    ref = db.reference("conversations").child(conversationID)
    if sender == conversationID.split("_")[0]: 
        ref.push({
            "username": msg.username,
            "text": msg.text,
            "readForUser1": True,
            "readForUser2": msg.readForUser2, 
            "timestamp": msg.timestamp,
            "messageID": msg.messageID
        })
    elif sender == conversationID.split("_")[1]:
        ref.push({
            "username": msg.username,
            "text": msg.text,    
            "readForUser2": True,
            "readForUser1": msg.readForUser1,
            "timestamp": msg.timestamp,
            "messageID": msg.messageID
        })
    return {"status": "sent"}
@app.get("/conversations", response_model=List[Conversation])
def get_conversations():
    ref = db.reference("conversations")
    conversations = ref.get()
    if conversations:
        return list(conversations.values())
    return []
@app.get("/conversation")
def get_conversation(conversationID):
    ref = db.reference("conversations").child(conversationID)
    messages = ref.get()
    if messages:
        return list(messages.values())  # return list instead of dict
    return []



@app.put("/conversations/{conversationID}/messages/{messageID}/read")
def set_read(sender: str, conversationID: str, messageID: str):
    ref = db.reference("conversations").child(conversationID)
    messages = ref.get()

    if not messages:
        return {"status": "not found"}

    for key, msg in messages.items():
        if msg.get("messageID") == messageID:

            if sender == conversationID.split("_")[0]:
                ref.child(key).update({"readForUser1": True})
            elif sender == conversationID.split("_")[1]:
                ref.child(key).update({"readForUser2": True})

            return {"status": "updated"}

    return {"status": "message not found"}

@app.get("/conversations/{conversationID}/unreadCount")
def get_unread_count(conversationID: str, user: str):
    ref = db.reference("conversations").child(conversationID)
    messages = ref.get()

    if not messages:
        return {"unreadCount": 0}

    user1, user2 = conversationID.split("_")

    if user == user1:
        unread_count = sum(
            1 for msg in messages.values()
            if not msg.get("readForUser1", False)
        )
    else:
        unread_count = sum(
            1 for msg in messages.values()
            if not msg.get("readForUser2", False)
        )

    return {"unreadCount": unread_count}

@app.post("/setOnline")
def set_online(username: str):
    ref = db.reference("onlineUsers").child(username)
    ref.set({
        "online": True,
        "lastSeen": time.time()
    })
    return {"status": "online"}

@app.post("/heartbeat")
def heartbeat(username: str):
    ref = db.reference("onlineUsers").child(username).child("lastSeen")
    ref.set(time.time())
    return {"status": "ok"}

@app.post("/setOffline")
def set_offline(username: str):
    ref = db.reference("onlineUsers").child(username)
    ref.delete()
    return {"status": "offline"}

@app.post("/newUser")
def create_user(username: str, password: str):

    ref = db.reference("UserDetails").child(username)

    if ref.get() is not None:
        return {"status": "error", "message": "user already exists"}

    ref.set({
        "username": username,
        "password": password
    })

    return {"status": "created"}

@app.post("/login")
def login(username: str, password: str):
    ref = db.reference("UserDetails").child(username)
    user = ref.get()

    # User doesn't exist → create it
    if user is None:
        ref.set({
            "username": username,
            "password": password
        })
        return {"status": "created"}

    # User exists → check password
    if user["password"] == password:
        return {"status": "success"}

    return {"status": "wrong_password"}

@app.get("/onlineUsers")
def online_users():
    ref = db.reference("onlineUsers")
    users = ref.get()
    if users:
        current_time = time.time()
        online_users = []
        for username, data in users.items():
            if isinstance(data, dict) and data.get("lastSeen", 0) > current_time - 30:
                online_users.append(username)
        return online_users
    return []

@app.get("/UserDetails")
def get_all_users():
    ref = db.reference("UserDetails")
    users = ref.get()
    if users:
        return list(users.keys())
    return []

@app.get("/")
def root():
    return {"status": "server running"}