package com.example.model

case class Message(senderId: String, receiverId: String, content: String, messageType: String)

object MessageType {
  val TASK_REQUEST = "TASK_REQUEST"
  val TASK_RESPONSE = "TASK_RESPONSE"
  val STATUS_UPDATE = "STATUS_UPDATE"
}