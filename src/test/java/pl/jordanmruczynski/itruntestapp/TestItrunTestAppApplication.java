package pl.jordanmruczynski.itruntestapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestItrunTestAppApplication {

	public static void main(String[] args) {
		SpringApplication.from(ItrunTestAppApplication::main).with(TestItrunTestAppApplication.class).run(args);
	}

}
