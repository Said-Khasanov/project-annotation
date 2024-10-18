package ru.khasanov.projectannotation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ProjectAnnotationApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ProjectAnnotationApplication.class, args);
        context.getBean(SomeService.class).print(5, "Hello World");
    }

}
