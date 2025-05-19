package com.example.coordination

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import scala.concurrent.duration._

class CircuitBreaker(maxFailures: Int, resetTimeout: FiniteDuration) {
  private var failureCount = 0
  private var isOpen = false
  private var lastFailureTime: Long = 0

  def call[T](block: => T): Option[T] = {
    if (isOpen) {
      if (System.currentTimeMillis() - lastFailureTime > resetTimeout.toMillis) {
        reset()
      } else {
        None // Circuit is open, do not execute
      }
    }

    try {
      val result = Some(block)
      reset() // Reset on successful call
      result
    } catch {
      case _: Throwable =>
        recordFailure()
        None // Return None on failure
    }
  }

  private def recordFailure(): Unit = {
    failureCount += 1
    lastFailureTime = System.currentTimeMillis()
    if (failureCount >= maxFailures) {
      openCircuit()
    }
  }

  private def openCircuit(): Unit = {
    isOpen = true
  }

  private def reset(): Unit = {
    failureCount = 0
    isOpen = false
  }
}