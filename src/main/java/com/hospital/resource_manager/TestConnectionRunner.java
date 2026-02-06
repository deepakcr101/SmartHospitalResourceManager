package com.hospital.resource_manager;



import org.neo4j.driver.Driver;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConnectionRunner {
  
	@Bean
	public CommandLineRunner demo(Driver driver) {
		return args -> {
			try(var session = driver.session()){
				String query = "CALL dbms.components() YIELD versions UNWIND versions AS version " +
			               "RETURN version LIMIT 1";
			    String version = session.run(query).single().get("version").asString();


				System.out.println("Connection Successful"+ version);
			}
			catch(Exception e) {
				System.err.println("Connection Failed: "+ e.getMessage());
			}
		};
	}
}
