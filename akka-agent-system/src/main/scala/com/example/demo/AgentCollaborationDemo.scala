package com.example.demo

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import com.example.agent.{Agent, AgentCapability, AgentManager, RegisterAgent}
import com.example.model.{Task, Message, PrintSystemStatus, AgentStatusUpdate, QueueStatusUpdate}
import scala.concurrent.duration._
import scala.concurrent.Await
import akka.util.Timeout
import java.util.UUID
import com.example.coordination.{TaskSequenceCoordinator, StartProcess, TaskComplete, KnowledgeRepository}

object AgentCollaborationDemo {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("AgentCoordinationSystem")
    val agentManager = system.actorOf(AgentManager.props(system), "agentManager")

    val textAnalysisAgent = system.actorOf(
      Agent.props("text-analysis", AgentCapability(10, Set("text-analysis", "sentiment-analysis"))), 
      "text-analysis"
    )
    val dataProcessingAgent = system.actorOf(
      Agent.props("data-processing", AgentCapability(2, Set("data-processing", "filtering"))),
      "data-processing"
    )
    val reasoningAgent = system.actorOf(
      Agent.props("reasoning", AgentCapability(5, Set("reasoning", "decision-making"))),
      "reasoning"
    )

    agentManager ! RegisterAgent("text-analysis", 
                               AgentCapability(5, Set("text-analysis", "sentiment-analysis")),
                               textAnalysisAgent)
    agentManager ! RegisterAgent("data-processing", 
                               AgentCapability(3, Set("data-processing", "filtering")),
                               dataProcessingAgent)
    agentManager ! RegisterAgent("reasoning", 
                               AgentCapability(2, Set("reasoning", "decision-making")),
                               reasoningAgent)

    Thread.sleep(1000)

    val knowledgeRepository = system.actorOf(Props(new KnowledgeRepository()), "knowledgeRepository")
    val systemMonitor = system.actorOf(Props(new SystemMonitor(system)), "systemMonitor")
    systemMonitor ! QueueStatusUpdate(0, 0)

    import system.dispatcher
    system.scheduler.scheduleAtFixedRate(1.seconds, 1.seconds) {
      () => systemMonitor ! PrintSystemStatus
    }

    println("Creating a knowledge-sharing ecosystem among agents...")

    textAnalysisAgent ! Message(
      "system", 
      "text-analysis", 
      "Share your text analysis models with data-processing", 
      "SYSTEM_INSTRUCTION"
    )

    val initialTasks = List(
      Task(s"initial-1", "Extract entities from the document text", Set("text-analysis"), 2),
      Task(s"initial-2", "Process extracted entities and compute statistics", Set("data-processing"), 2),
      Task(s"initial-3", "Generate insights from processed data", Set("reasoning"), 2)
    )
    submitTaskPipeline(system, initialTasks)

    system.scheduler.scheduleAtFixedRate(1.seconds, 3.seconds) { () => 
      println("\nAuto-generating a new pipeline...")
      submitRandomPipeline(system)
    }

    println("Multi-step document processing pipeline started.")
    println("System is running and ready to accept more tasks.")
    println("Available commands: task [capability], pipeline, status, exit")

    var continueRunning = true
    while (continueRunning) {
      try {
        val input = Option(scala.io.StdIn.readLine("> "))
        input match {
          case Some(cmd) if cmd != null =>
            println(s"Received command: $cmd")
            continueRunning = handleCommand(cmd, system)
          case _ =>
            Thread.sleep(100)
        }
      } catch {
        case e: Exception => 
          println(s"Error handling input: ${e.getMessage}")
          e.printStackTrace()
      }
    }

    implicit val timeout: Timeout = 5.seconds
    Await.result(system.terminate(), timeout.duration)
  }

  private def handleCommand(cmd: String, system: ActorSystem): Boolean = {
    cmd.trim.toLowerCase match {
      case "exit" | "quit" => 
        println("Shutting down...")
        false
      case s"task $capability" =>
        val taskId = UUID.randomUUID().toString
        val task = Task(taskId, s"Dynamic task for $capability", Set(capability), 1)
        system.actorSelection("/user/agentManager") ! task
        println(s"Submitted individual task $taskId for capability: $capability")
        true
      case "pipeline" =>
        submitRandomPipeline(system)
        true
      case "status" =>
        system.actorSelection("/user/systemMonitor") ! PrintSystemStatus
        true
      case _ =>
        println("Unknown command. Available: task [capability], pipeline, status, exit")
        true
    }
  }

  private def submitRandomPipeline(system: ActorSystem): Unit = {
    val pipelineId = UUID.randomUUID().toString
    val tasks = List(
      Task(s"$pipelineId-1", "Dynamic text analysis", Set("text-analysis"), 2),
      Task(s"$pipelineId-2", "Dynamic data processing", Set("data-processing"), 2),
      Task(s"$pipelineId-3", "Dynamic reasoning", Set("reasoning"), 2)
    )
    submitTaskPipeline(system, tasks)
  }

  private def submitTaskPipeline(system: ActorSystem, tasks: List[Task]): Unit = {
    val pipelineId = UUID.randomUUID().toString
    println(s"Creating pipeline: $pipelineId with ${tasks.size} tasks")
    val coordinator = system.actorOf(
      TaskSequenceCoordinator.props(pipelineId, tasks),
      s"coordinator-$pipelineId"
    )
    coordinator ! StartProcess
  }
}
