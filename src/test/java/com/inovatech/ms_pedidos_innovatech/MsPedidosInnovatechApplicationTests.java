package com.inovatech.ms_pedidos_innovatech;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "jwt.secret=test-secret-for-testing-purposes-only-min32c",
    "spring.rabbitmq.listener.simple.auto-startup=false"
})
class MsPedidosInnovatechApplicationTests {

	@Test
	void contextLoads() {
	}

}
