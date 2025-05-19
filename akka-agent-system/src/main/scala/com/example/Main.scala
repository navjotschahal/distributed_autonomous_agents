package com.example

import akka.actor.{ActorSystem, Props}
import com.example.agent.AgentManager

object Main extends App {
  // Initialize the Akka Actor System
  val system: ActorSystem = ActorSystem("akka-agent-system")

  // Create and start the AgentManager
  val agentManager = system.actorOf(Props.create(classOf[AgentManager], system), "agentManager")

  // You can add additional initialization logic here if needed

  // Shutdown the system when done
  sys.addShutdownHook {
    system.terminate()
  }
}