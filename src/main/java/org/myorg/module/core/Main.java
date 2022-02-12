package org.myorg.module.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(
        scanBasePackages = "org.myorg"
)
// TODO Переписать
@PropertySource(
        value = { "modules.application.properties",
                "auth.application.properties",
                "core.application.properties"}
)
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
