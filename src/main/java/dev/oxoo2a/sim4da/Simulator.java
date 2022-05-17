package dev.oxoo2a.sim4da;

import java.io.PrintStream;

public class Simulator {
    
    private final int numberOfNodes;
    private final Tracer tracer;
    private final Network network;
    private final Node[] nodes;
    
    public Simulator(int numberOfNodes, String name, boolean ordered, boolean enableTracing, boolean useLog4j2,
                     PrintStream alternativeDestination) {
        this.numberOfNodes = numberOfNodes;
        tracer = new Tracer(name, ordered, enableTracing, useLog4j2, alternativeDestination);
        network = new Network(numberOfNodes, tracer);
        nodes = new Node[numberOfNodes];
    }
    
    public static Simulator createDefaultSimulator(int numberOfNodes) {
        return new Simulator(numberOfNodes, "sim4da", true, true, true, System.out);
    }
    
    public static Simulator createSimulatorUsingLog4j2(int numberOfNodes) {
        return new Simulator(numberOfNodes,"sim4da", true, true, true, null);
    }
    
    public void attachNode(int id, Node node) {
        if (id>=0 && id<numberOfNodes) nodes[id]=node;
    }
    
    public void runSimulation(int duration) throws InstantiationException {
        // Check that all nodes are attached
        for (Node node : nodes) {
            if (node==null) throw new InstantiationException();
            node.setNetwork(network);
            node.setTracer(tracer);
        }
        tracer.emit("Simulator::runSimulation with %d nodes for %d seconds", numberOfNodes, duration);
        for (Node node : nodes) {
            node.start();
        }
        try {
            Thread.sleep(duration * 1000L); // Wait for the required duration
        } catch (InterruptedException ignored) {}
        network.stop(); // Stop network - release nodes waiting in receive ...
        for (Node node : nodes) { // Tell all nodes to stop and wait for the threads to terminate
            node.stop();
        }
        tracer.emit("Simulator::runSimulation finished");
    }
}
