package thesismanagement.ls1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ThesisManagementApplication implements ApplicationRunner {
	private static final Logger logger = LoggerFactory.getLogger(ThesisManagementApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ThesisManagementApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments applicationArguments) {
		logger.info("Service running...");
	}
}
