package com.example.coordination

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestActor, TestKit, TestProbe}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class TaskCoordinatorSpec extends TestKit(ActorSystem("TaskCoordinatorSpec"))
  with AnyWordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A TaskCoordinator" should {
    "assign tasks to agents based on their capabilities" in {
      val taskCoordinator = system.actorOf(Props[TaskCoordinator])
      val agentProbe = TestProbe()

      // Simulate agent registration
      taskCoordinator ! RegisterAgent(agentProbe.ref, AgentCapability(5, List("taskType1")))
      
      // Simulate task assignment
      val task = Task("taskType1", "Task data")
      taskCoordinator ! AssignTask(task)

      // Verify that the task was sent to the agent
      agentProbe.expectMsg(task)
    }

    "not assign tasks to agents that cannot handle them" in {
      val taskCoordinator = system.actorOf(Props[TaskCoordinator])
      val agentProbe = TestProbe()

      // Simulate agent registration with different capabilities
      taskCoordinator ! RegisterAgent(agentProbe.ref, AgentCapability(5, List("taskType2")))

      // Simulate task assignment
      val task = Task("taskType1", "Task data")
      taskCoordinator ! AssignTask(task)

      // Verify that the agent did not receive the task
      agentProbe.expectNoMessage()
    }

    "handle task completion and reassign if necessary" in {
      val taskCoordinator = system.actorOf(Props[TaskCoordinator])
      val agentProbe = TestProbe()

      // Simulate agent registration
      taskCoordinator ! RegisterAgent(agentProbe.ref, AgentCapability(5, List("taskType1")))

      // Simulate task assignment
      val task = Task("taskType1", "Task data")
      taskCoordinator ! AssignTask(task)

      // Simulate task completion
      agentProbe.send(taskCoordinator, TaskCompleted(task))

      // Verify that the coordinator can handle the completion
      // Additional assertions can be added here based on the expected behavior
    }
  }
}