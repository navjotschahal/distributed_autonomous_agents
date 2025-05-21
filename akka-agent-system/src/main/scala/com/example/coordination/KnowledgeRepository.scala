package com.example.coordination

import akka.actor.{Actor, ActorLogging}
import scala.collection.mutable

// Create a shared knowledge repository
class KnowledgeRepository extends Actor with ActorLogging {
  private val knowledgeBase = mutable.Map[String, Any]()
  
  override def receive: Receive = {
    case StoreKnowledge(key, value) =>
      knowledgeBase.put(key, value)
      log.info(s"Knowledge stored: $key")
      
    case RetrieveKnowledge(key) =>
      sender() ! KnowledgeResponse(key, knowledgeBase.get(key))
  }
}

case class StoreKnowledge(key: String, value: Any)
case class RetrieveKnowledge(key: String)
case class KnowledgeResponse(key: String, value: Option[Any])


