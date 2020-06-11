package com.example.jdbcdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//This Application class implements Spring Boot’s CommandLineRunner,
// which means it will execute the run() method after the application context is loaded.
@SpringBootApplication
public class JdbcdemoApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(JdbcdemoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(JdbcdemoApplication.class, args);
	}

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public void run(String... args) throws Exception {
		log.info("Creating Tables");

		jdbcTemplate.execute("DROP TABLE customers IF EXISTS ");
		jdbcTemplate.execute("CREATE TABLE customers(" + "id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");

		//Splits the names and puts it in an array of first and last names
		List<Object[]> splitUpNames = Arrays.asList("Shubhang Mehrotra", "Udai Agarwal", "Sajal Mehrotra", "Shubhang Jain", "Sajal Agarwal").stream().map(name -> name.split(" ")).collect(Collectors.toList());

		//prints out a each tuple from the array
		splitUpNames.forEach(name -> log.info(String.format("Inserting customer record for %s %s", name[0], name[1])));

		jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", splitUpNames);

		log.info("Querying for customer records where first_name = 'Shubhang':");
		jdbcTemplate.query("SELECT id, first_name, last_name FROM customers WHERE first_name = ?", new Object[]{"Shubhang"}, (rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"))
		).forEach(customer -> log.info(customer.toString()) );
	}

}
