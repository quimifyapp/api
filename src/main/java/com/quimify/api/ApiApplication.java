package com.quimify.api;

import com.quimify.api.inorganico.InorganicoModel;
import com.quimify.api.inorganico.InorganicoService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.PostLoad;

// Esta clase viene generada por la librería Spring Boot y contiene el main.

@SpringBootApplication
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

}
