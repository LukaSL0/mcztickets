package com.lukasl.orders;

import java.sql.Connection;
import javax.sql.DataSource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
public class OrdersApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrdersApplication.class, args);
		System.out.println("Application started on port 8083");
	}

	@Bean
    public CommandLineRunner testConnection(DataSource dataSource) {
        return args -> {
            try (Connection conn = dataSource.getConnection()) {
                System.out.println("Database connected successfully!");
                System.out.println("Connection: " + conn.getMetaData().getURL());
            } catch (Exception e) {
                System.err.println("Database connection failed.");
                e.printStackTrace();
            }
        };
    }

}
