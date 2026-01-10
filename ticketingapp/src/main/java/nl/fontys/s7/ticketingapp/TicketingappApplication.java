package nl.fontys.s7.ticketingapp;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TicketingappApplication {

	// Use this class as the logger name
	private static final Logger log = LoggerFactory.getLogger(TicketingappApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(TicketingappApplication.class, args);
	}

	@PostConstruct
	public void checkAppInsightsEnv() {
		String cs = System.getenv("APPLICATIONINSIGHTS_CONNECTION_STRING");
		boolean present = cs != null && !cs.isBlank();

		log.info("APPLICATIONINSIGHTS_CONNECTION_STRING present? {}", present);

		if (!present) {
			log.warn("APPLICATIONINSIGHTS_CONNECTION_STRING is NOT set or is blank inside the container.");
		}
	}
}
