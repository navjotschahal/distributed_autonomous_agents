package com.example.model

import akka.actor.ActorRef

// A wrapper for tasks that preserves the original sender
case class TaskEnvelope(task: Task, originalSender: ActorRef)