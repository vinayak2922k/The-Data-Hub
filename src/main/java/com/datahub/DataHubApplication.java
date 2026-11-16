package com.datahub;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Main Entry Point for The Data Hub RESTful API Server.
 * Configured to run on port 5000 by default (or via PORT environment variable).
 */
@SpringBootApplication
public class DataHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataHubApplication.class, args);
    }

    /**
     * Startup runner to print a stunning, colorful ANSI banner in the terminal.
     */
    @Bean
    public CommandLineRunner startupBanner(@Value("${server.port:5000}") String port) {
        return args -> {
            String cyan = "\u001B[36m";
            String purple = "\u001B[35m";
            String green = "\u001B[32m";
            String yellow = "\u001B[33m";
            String bold = "\u001B[1m";
            String reset = "\u001B[0m";

            System.out.println(purple + bold);
            System.out.println("  _____ _            ____        _          _   _       _     ");
            System.out.println(" |_   _| |__   ___  |  _ \\  __ _| |_ __ _  | | | |_   _| |__  ");
            System.out.println("   | | | '_ \\ / _ \\ | | | |/ _` | __/ _` | | |_| | | | | '_ \\ ");
            System.out.println("   | | | | | |  __/ | |_| | (_| | || (_| | |  _  | |_| | |_) |");
            System.out.println("   |_| |_| |_|\\___| |____/ \\__,_|\\__\\__,_| |_| |_|\\__,_|_.__/ ");
            System.out.println(reset);

            System.out.println(cyan + "=================================================================" + reset);
            System.out.println(green + bold + "   ★ The Data Hub REST API Server is ONLINE and Ready!" + reset);
            System.out.println(cyan + "=================================================================" + reset);
            System.out.println("   " + yellow + ">> Port         : " + reset + port);
            System.out.println("   " + yellow + ">> Local URL    : " + reset + "http://localhost:" + port);
            System.out.println("   " + yellow + ">> API Explorer : " + reset + "http://localhost:" + port + "/");
            System.out.println("   " + yellow + ">> Health Check : " + reset + "http://localhost:" + port + "/health");
            System.out.println("   " + yellow + ">> Blog Posts   : " + reset + "http://localhost:" + port + "/posts");
            System.out.println("   " + yellow + ">> Auth Login   : " + reset + "http://localhost:" + port + "/login");
            System.out.println(cyan + "=================================================================" + reset);
            System.out.println(reset);
        };
    }
}

