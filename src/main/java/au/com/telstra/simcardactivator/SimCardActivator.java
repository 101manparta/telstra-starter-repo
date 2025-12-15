package au.com.telstra.simcardactivator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class SimCardActivator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SimCardActivator.class);

    public static void main(String[] args) {
        SpringApplication.run(SimCardActivator.class, args);
        LOGGER.info("SIM Card Activator application started successfully!");
    }
}