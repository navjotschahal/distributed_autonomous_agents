package com.example.protocol

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MessageProtocolSpec extends AnyFlatSpec with Matchers {

  "MessageProtocol" should "correctly define the structure of messages" in {
    val message = Message("test", "Hello, Agent!")
    message.id shouldEqual "test"
    message.content shouldEqual "Hello, Agent!"
  }

  it should "serialize and deserialize messages correctly" in {
    val originalMessage = Message("test", "Hello, Agent!")
    val serialized = MessageSerializer.serialize(originalMessage)
    val deserializedMessage = MessageSerializer.deserialize(serialized)

    deserializedMessage shouldEqual originalMessage
  }

  it should "handle invalid message formats gracefully" in {
    val invalidData = "invalid data"
    val deserializedMessage = MessageSerializer.deserialize(invalidData)

    deserializedMessage shouldBe None
  }
}