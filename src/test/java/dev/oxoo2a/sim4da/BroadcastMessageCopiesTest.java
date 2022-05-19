package dev.oxoo2a.sim4da;

import dev.oxoo2a.sim4da.Simulator.TimestampType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.oxoo2a.sim4da.Message.MessageType;

public class BroadcastMessageCopiesTest {
    
    private static final int numberOfNodes = 5;
    private static final int duration = 2;
    
    private static class TestNode extends Node {
        public TestNode(Simulator s, int id) {
            super(s, id);
        }
        @Override
        public void run() {
            JsonSerializableMap m = new JsonSerializableMap();
            if (id==0) {
                m.put("Sender", String.valueOf(id));
                m.put("Candidate", Integer.toString((id+1) % getNumberOfNodes()));
                sendBroadcast(m);
            }
            while (isStillSimulating()) {
                Message message = receive();
                if (message==null) {
                    Assertions.assertFalse(isStillSimulating());
                    break; // Null == Simulation time ends while waiting for a message
                }
                Assertions.assertEquals(message.getReceiverId(), id);
                Assertions.assertSame(message.getType(), MessageType.BROADCAST);
                m = JsonSerializableMap.fromJson(message.getPayload());
                int senderId = Integer.parseInt(m.get("Sender"));
                Assertions.assertEquals(senderId, message.getSenderId());
                int candidateId = Integer.parseInt(m.get("Candidate"));
                if (candidateId==id) {
                    Assertions.assertEquals(candidateId, message.getReceiverId());
                    m.put("Sender", String.valueOf(id));
                    m.put("Candidate", Integer.toString((id+1) % getNumberOfNodes()));
                    sendBroadcast(m);
                }
            }
        }
    }
    
    @Test
    public void areMessagesCopied () {
        Simulator s = new Simulator(numberOfNodes, TimestampType.EXTENDED_LAMPORT, "amc", true, true, System.out);
        for (int id = 0; id<numberOfNodes; id++) {
            Node n = new TestNode(s, id);
            s.attachNode(n);
        }
        try {
            s.runSimulation(duration);
        } catch (InstantiationException ignored) {
            Assertions.fail("Not all nodes instantiated");
        }
    }
}
