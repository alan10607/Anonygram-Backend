package com.alan10607.ag;

import org.junit.jupiter.api.Test;
import org.slf4j.helpers.MessageFormatter;

import java.util.HashSet;
import java.util.Set;

//@SpringBootTest
class AnonygramApplicationTests {
	
	@Test
	void test() {
		Set<Integer> s1 = new HashSet<>();
		s1.add(1);
		s1.add(2);
		s1.add(3);
		Set<Integer> s2 = new HashSet<>();
		s2.add(2);
		s2.add(3);
		s2.add(4);
		s2.add(5);

		s1.retainAll(s2);
		s1.removeAll(s2);

	}

}
