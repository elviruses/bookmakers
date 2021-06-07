package org.example.bookmaker;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("org.example.bookmaker")
@PropertySource("classpath:application.properties")
public class SpringConfig {
}
