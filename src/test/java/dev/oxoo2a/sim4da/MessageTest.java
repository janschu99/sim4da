package dev.oxoo2a.sim4da;

//import static org.junit.jupiter.api.Assertions.assertEquals;

//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
public class MessageTest {

	//@BeforeAll
	static void setup () {
		Message m = new Message().add("a","b");
		String s = m.toJson();
	}
}
