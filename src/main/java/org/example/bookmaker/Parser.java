package org.example.bookmaker;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
public class Parser {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "applicationContext.xml");

        Zenit zenitGen = context.getBean("zenitBean", Zenit.class);
        Zenit zenitSecond = context.getBean("zenitBean", Zenit.class);
        Result result = context.getBean("resultBean", Result.class);

        zenitGen.setType("general");
        zenitSecond.setType("second");
        zenitGen.start();
        zenitSecond.start();
        result.start();
    }
}
