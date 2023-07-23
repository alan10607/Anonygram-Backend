package com.alan10607.ag;

import org.junit.jupiter.api.Test;
import org.slf4j.helpers.MessageFormatter;

//@SpringBootTest
class AnonygramApplicationTests {
	
	@Test
	void contextLoads() {
		MessageFormatter.arrayFormat("{} /{}", new Object[]{1,"s"});
	}

}
