package com.example

import akka.actor.{ActorSystem, Props}
import com.example.agent.AgentManager

object Main extends App {
  val system: ActorSystem = ActorSystem("akka-agent-system")

  val agentManager = system.actorOf(Props.create(classOf[AgentManager], system), "agentManager")


  // Shutdown the system when done
  sys.addShutdownHook {
    system.terminate()
  }
}