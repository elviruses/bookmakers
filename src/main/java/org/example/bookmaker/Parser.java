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


public class Parser {
    public static void main(String[] args) {
        int hour = 4;
        while (true) {
            if (DB.getInstance().getReadyMatch() != 0) {
                (new Zenit()).start();
            } else if (hour == 4) {
                (new Zenit()).start();
                hour = 0;
            }
            try {
                Thread.sleep(1000 * 900);
                hour++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
