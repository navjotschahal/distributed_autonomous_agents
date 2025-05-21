# Akka Agent System

This project implements a distributed autonomous agent system that coordinates multiple AI agents with varying capabilities using the Akka Actor framework in Scala. Developed as a solution to the "AI Agent Coordination System" challenge, it focuses on designing a robust communication protocol that enables agents to collaborate effectively while maintaining system stability.

## Project Structure

```
akka-agent-system
├── src
│   ├── main
│   │   ├── scala
│   │   │   └── com
│   │   │       └── example
│   │   │           ├── Main.scala
│   │   │           ├── agent
│   │   │           │   ├── Agent.scala
│   │   │           │   ├── AgentManager.scala
│   │   │           │   ├── AgentCapability.scala
│   │   │           │   └── AgentRegistry.scala
│   │   │           ├── coordination
│   │   │           │   ├── TaskCoordinator.scala
│   │   │           │   ├── TaskSequenceCoordinator.scala
│   │   │           │   ├── WorkloadDistributor.scala
│   │   │           │   ├── KnowledgeRepository.scala
│   │   │           │   └── CircuitBreaker.scala
│   │   │           ├── protocol
│   │   │           │   ├── MessageProtocol.scala
│   │   │           │   ├── MessageRouter.scala
│   │   │           │   └── MessageSerializer.scala
│   │   │           ├── model
│   │   │           │   ├── Task.scala
│   │   │           │   ├── Message.scala
│   │   │           │   ├── AgentMessages.scala
│   │   │           │   └── AgentStatus.scala
│   │   │           └── demo
│   │   │               ├── AgentCollaborationDemo.scala
│   │   │               └── SystemMonitor.scala
│   │   └── resources
│   │       ├── application.conf
│   │       └── logback.xml
│   └── test
│       └── scala
│           └── com
│               └── example
│                   ├── agent
│                   │   └── AgentSpec.scala
│                   ├── coordination
│                   │   └── TaskCoordinatorSpec.scala
│                   └── protocol
│                       └── MessageProtocolSpec.scala
├── build.sbt
└── README.md
```

## Features

- **Modular Agent Architecture**: The system supports different types of specialized AI agents (text-analysis, data-processing, reasoning) that can be dynamically registered and unregistered.
- **Dynamic Workload Distribution**: Tasks are assigned to agents based on their capabilities and current load, with a priority queue mechanism for handling high-priority tasks first.
- **Knowledge Sharing**: A central knowledge repository allows agents to store and retrieve shared information, facilitating collaboration.
- **Multi-step Task Pipelines**: Complex workflows can be orchestrated through the TaskSequenceCoordinator which manages sequences of dependent tasks.
- **Fault Tolerance**: Circuit breaker pattern implementation prevents system overload and provides graceful degradation during failures.
- **Real-time System Monitoring**: Visual monitoring of agent status, task queue, and overall system performance through the SystemMonitor.
- **Interactive Demo**: A comprehensive demo showcasing how agents collaborate to complete tasks with both automated and user-initiated scenarios.

## Getting Started

1. **Clone the repository**:
   ```sh
   git clone https://github.com/yourusername/akka-agent-system.git
   cd akka-agent-system
   ```

2. **Build the project**:
   ```sh
   sbt clean compile
   ```

3. **Run the demo**:
   ```sh
   sbt run
   ```

## Usage

The system can be used for various collaborative AI tasks, such as:

- **Document Analysis Pipeline**: Text analysis → Data processing → Reasoning agents working in sequence
- **Parallel Task Processing**: Multiple agents handling different aspects of a complex problem simultaneously
- **Dynamic Workload Balancing**: Intelligent distribution of tasks based on agent capabilities and current load
- **Fault-tolerant Processing**: System maintains stability even when individual agents fail or become overloaded
- **Knowledge Sharing**: Agents can contribute to and benefit from a shared knowledge repository
- **Multi-step Workflows**: Complex tasks can be broken down into sequences of simpler tasks distributed across specialized agents

## How the System Addresses the Challenge

1. **Modular Agent Coordination**:
   - The system uses Akka's actor model to create isolated, message-passing agents.
   - Each agent has defined capabilities and can communicate with others through the AgentManager.

2. **Dynamic Workload Distribution**:
   - AgentRegistry tracks agent capabilities and current load.
   - Tasks are assigned based on agent specialization and current workload.
   - Priority queue ensures important tasks are processed first.

3. **Coordination Protocol**:
   - Defined message types in MessageProtocol for standardized communication.
   - Error handling with circuit breaker pattern for fault tolerance.
   - Task completion notifications and knowledge sharing mechanisms.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any enhancements or bug fixes.

## License

This project is licensed under the MIT License. See the LICENSE file for details.