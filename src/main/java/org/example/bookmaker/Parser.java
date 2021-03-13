package org.example.bookmaker;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class Parser {

    private static final Logger logger = LoggerFactory.getLogger(Parser.class);

    public static void main(String[] args) {
        int hour = 4;
        while (true) {
            if (DB.getInstance().getReadyMatch() != 0) {
                (new Zenit()).start();
                logger.info("Запущен 15-минутный run");
            } else if (hour >= 4) {
                (new Zenit()).start();
                logger.info("Запущен часовой run");
                hour = 0;
            }
            try {
                Thread.sleep(1000 * 900);
                hour++;
            } catch (InterruptedException e) {
                logger.error(e.toString());
                e.printStackTrace();
            }
        }
    }
}
