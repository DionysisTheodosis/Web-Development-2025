package gr.aegean.icsd.autorepair;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Configurable
@SpringBootApplication
public class AutorepairApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutorepairApplication.class, args);
	}

}

