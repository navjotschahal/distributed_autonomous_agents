package com.example.demo

import akka.actor.ActorSystem
import com.example.agent.{AgentCapability, AgentManager, RegisterAgent}
import com.example.model.Task
import scala.concurrent.duration._
import scala.concurrent.Await
import akka.util.Timeout

object AgentCollaborationDemo {
  def main(args: Array[String]): Unit = {
    // Create the actor system
    val system = ActorSystem("AgentCoordinationSystem")
    
    // Create the agent manager
    val agentManager = system.actorOf(AgentManager.props(system), "agentManager")
    
    // Create various agents with different capabilities
    val textAnalysisAgent = system.actorOf(
      com.example.agent.Agent.props("text-analysis", AgentCapability(5, Set("text-analysis", "sentiment-analysis"))), 
      "text-analysis"  // Match this with registration ID
    )
    
    val dataProcessingAgent = system.actorOf(
      com.example.agent.Agent.props("data-processing", AgentCapability(3, Set("data-processing", "filtering"))),
      "data-processing"  // Match this with registration ID
    )
    
    val reasoningAgent = system.actorOf(
      com.example.agent.Agent.props("reasoning", AgentCapability(2, Set("reasoning", "decision-making"))),
      "reasoning"  // Match this with registration ID
    )
    
    // Register agents with the manager (using the SAME IDs and providing ActorRefs)
    agentManager ! RegisterAgent("text-analysis", 
                                 AgentCapability(5, Set("text-analysis", "sentiment-analysis")),
                                 textAnalysisAgent)
    agentManager ! RegisterAgent("data-processing", 
                                 AgentCapability(3, Set("data-processing", "filtering")),
                                 dataProcessingAgent)
    agentManager ! RegisterAgent("reasoning", 
                                 AgentCapability(2, Set("reasoning", "decision-making")),
                                 reasoningAgent)
    
    // Allow time for agents to register
    Thread.sleep(1000)
    
    // Create tasks
    val tasks = List(
      Task("task1", "Analyze this document for sentiment", Set("text-analysis"), 2),
      Task("task2", "Process this dataset", Set("data-processing"), 1),
      Task("task3", "Make a decision based on these factors", Set("reasoning"), 3)
    )
    
    // Submit tasks to the agent manager
    tasks.foreach(task => {
      println(s"Submitting task: ${task.id}")
      agentManager ! task
    })
    
    // Keep the application running for demonstration purposes
    println("Press ENTER to terminate...")
    scala.io.StdIn.readLine()
    
    // Terminate the actor system
    implicit val timeout: Timeout = 5.seconds
    Await.result(system.terminate(), timeout.duration)
  }
}