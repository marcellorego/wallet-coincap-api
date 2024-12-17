package com.example.demo;

import com.example.demo.test.SpringProfileRunnerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class WalletApplicationTests extends SpringProfileRunnerTest {

	@Test
	void contextLoads(@Value("${spring.application.name}") String applicationName,
					  @Value("${info.application.name}") String infoApplicationName) {
		assertThat(applicationName).isEqualTo(infoApplicationName)
				.isEqualTo("wallet-service");
	}

}
