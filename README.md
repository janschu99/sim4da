# sim4da - Simulator for Distributed Algorithms

The simulator strives to abstract from most of the details in network programming and to ease the development of algorithm simulations. The simulator core is self-contained in a Java module `dev.oxoo2a.sim4da`.

## Writing your own simulation

The distributed algorithm simulation is controlled by an instance of class `Simulator`:

```Java
    Simulator s = new Simulator(numberOfNodes);
    for (int id = 0; id<numberOfNodes; id++) {
      Node n = new ApplicationNode(s, id);
      s.attachNode(n);
    }
    s.runSimulation(duration);
```

By instantiating a Simulator object, a network of `numberOfNodes` nodes is created. For each node `id` between `[0, numberOfNodes)` the code for the given `id` must be attached to the simulator. This `ApplicationNode` is derived from the abstract class `Node` which provides all the required functionality for implementing the algorithm simulation (see below). Finally, the simulation can be executed for `duration` seconds.

Extending class `Node` enables the implementation of the intended distributed algorithm by implementing the method `run`:

```Java
    public void run() {
      ...
    }
```

Inside `run`, the following fields and methods are available:

- `id`: the `id` of this node
- `getNumberOfNodes()`: returns the total number of nodes in the simulator
- `sendUnicast(receiverId, String messageContent)`: sends a raw string `messageContent` to node `receiverId`
- `sendUnicast(receiverId, JsonSerializableMap messageContent)`: sends a `HashMap`-like message encoded in JSON to node `receiverId`
- `sendBroadcast(String messageContent)`: sends a raw string `messageContent` to all nodes (except the sender itself)
- `sendBroadcast(JsonSerializableMap messageContent)`: sends a `HashMap`-like message encoded in JSON to all nodes (except the sender itself)
- `isStillSimulating()`: returns `true` while the duration for the simulation is not exceeded
- `receive()`: blocks until a message is received by the node or the simulation time has ended (in which case `null` is returned). Otherwise, returns an object of type `Message`, which stores `senderId`, `receiverId`, `type` (unicast or broadcast) and the `payload` as a string. If a `JsonSerializableMap` is expected, this payload must be deserialized from JSON back into an object of type `JsonSerializableMap` by calling `JsonSerializableMap.fromJson(payload)`.

An example implementation for a distributed algorithm simulation is available as a test case (`BroadcastNode.java` and `SimulatorTest.java`).

## Building the sim4da jar

`Gradle` is used as the build tool. `gradle build` and `gradle shadowJar` within the root directory of the repository generate the required jar files. The fat jar contains the transitive dependencies to `gson`, `junit` and `log4j2`.

## Logging

The simulator core can create trace files of every simulation thanks to the logging framework `log4j2`. A default configuration file is part of the simulator resources. The default logging configuration is similar to:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <Console name="console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n" />
        </Console>

        <File name="logfile" fileName="sim4da-app.log" append="false">
            <PatternLayout pattern="%msg%n"/>
        </File>
    </Appenders>
    <Loggers>
        <Logger name="sim4da" level="trace" additivity="false">
            <AppenderRef ref="logfile"/>
        </Logger>
        <Root level="warn">
            <AppenderRef ref="console"/>
        </Root>
    </Loggers>
</Configuration>
```