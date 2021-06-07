package org.example.bookmaker;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
@Scope("prototype")
public class Result {
    private static final Logger logger = LoggerFactory.getLogger(Result.class);

    private JSONObject json;
    private int reload;

    @Value("${result.url_json}")
    private String URL_JSON;
    @Value("${result.code}")
    private String CODE;
    @Value("${result.error}")
    private String ERROR;
    @Value("${result.result}")
    private String RESULT;
    @Value("${zenit.games}")
    private String GAMES;
    @Value("${result.score}")
    private String SCORE;
    @Value("${result.cancel}")
    private String CANCEL;
    @Value("${result.date}")
    private String DATE;
    @Value("${result.team_1_name}")
    private String TEAM_1_NAME;
    @Value("${result.team_2_name}")
    private String TEAM_2_NAME;
    @Value("${result.date_pattern}")
    private String DATE_PATTERN;



    private LocalDateTime dateTimeMatch;
    private String teamOne;
    private String teamTwo;
    private int tone1 = -99;
    private int tone2 = -99;
    private int ttwo1 = -99;
    private int ttwo2 = -99;
    private String canc = "-";
    private final Map<String, Result> mapResult = new HashMap<>();

    public Result(Result result) {
        this.tone1 = result.getTone1();
        this.tone2 = result.getTone2();
        this.ttwo1 = result.getTtwo1();
        this.ttwo2 = result.getTtwo2();
        this.canc = result.getCanc();
    }

    public Result() {
    }

    public void loadResult() {
        try {
            String fromDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-M-d"));
            String toDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-M-d"));
            String url = String.format(URL_JSON, fromDate, toDate);
            logger.info("URL получения результатов: {}", url);
//            System.out.println("URL получения результатов:" + url);
            do {
                try {
                    json = getJson(url);
                    reload = 0;
                } catch (IOException e) {
                    logger.info("Ошибка получения JSON результатов, попытка: {}", reload);
                    logger.error(e.getMessage(), e);
//                    e.printStackTrace();
                    reload++;
                }
            } while (json == null && reload < 5);
            if (json.has(CODE) && json.get(CODE).equals(ERROR)) {

            } else {
                loadMapResult();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
//            e.printStackTrace();
        }
    }

    public JSONObject getJson(String url) throws IOException {
        return new JSONObject(Jsoup.connect(url).ignoreContentType(true).execute().body());
    }

    private void loadMapResult() {
        for (String key: json.getJSONObject(RESULT).getJSONObject(GAMES).keySet()) {
            String[] result = replacer(json.getJSONObject(RESULT).getJSONObject(GAMES).getJSONObject(key).get(SCORE).toString());
            if (result.length == 0 || result.length == 1) {
                tone1 = -99;
                tone2 = -99;
                ttwo1 = -99;
                ttwo2 = -99;
                canc = (json.getJSONObject(RESULT).has(CANCEL) &&
                        json.getJSONObject(RESULT).getJSONObject(CANCEL).has(key))
                        ? json.getJSONObject(RESULT).getJSONObject(CANCEL).get(key).toString()
                        : "-";
            } else if (result.length == 2) {
                tone1 = -99;
                tone2 = -99;
                ttwo1 = Integer.parseInt(result[0]);
                ttwo2 = Integer.parseInt(result[1]);
                canc = (json.getJSONObject(RESULT).has(CANCEL) &&
                        json.getJSONObject(RESULT).getJSONObject(CANCEL).has(key))
                        ? json.getJSONObject(RESULT).getJSONObject(CANCEL).get(key).toString()
                        : "-";
            } else {
                tone1 = Integer.parseInt(result[2]);
                tone2 = Integer.parseInt(result[3]);
                ttwo1 = Integer.parseInt(result[0]);
                ttwo2 = Integer.parseInt(result[1]);
                canc = (json.getJSONObject(RESULT).has(CANCEL) &&
                        json.getJSONObject(RESULT).getJSONObject(CANCEL).has(key))
                        ? json.getJSONObject(RESULT).getJSONObject(CANCEL).get(key).toString()
                        : "-";
            }

            String dateMatch = LocalDateTime.now().getYear() + "/" + json.getJSONObject(RESULT).getJSONObject(GAMES).getJSONObject(key).get(DATE).toString();
            dateTimeMatch = LocalDateTime.parse(dateMatch, DateTimeFormatter.ofPattern(DATE_PATTERN));
            teamOne = json.getJSONObject(RESULT).getJSONObject(GAMES).getJSONObject(key).get(TEAM_1_NAME).toString();
            teamTwo = json.getJSONObject(RESULT).getJSONObject(GAMES).getJSONObject(key).get(TEAM_2_NAME).toString();
            mapResult.put(dateTimeMatch + teamOne + teamTwo, new Result(this));

            logger.info("Key: {}{}{} | Value: {} {} {} {} {}", dateTimeMatch, teamOne, teamTwo
            ,this.tone1, this.tone2, this.ttwo1, this.ttwo2, this.canc);
        }
    }

    private String[] replacer(String string) {
        return string.replace(":", " ")
                     .replace("(", "")
                     .replace(")", "")
                     .split(" ");
    }

    public Map<String, Result> getMapResult() {
        return mapResult;
    }

    public int getTone1() {
        return tone1;
    }

    public int getTone2() {
        return tone2;
    }

    public int getTtwo1() {
        return ttwo1;
    }

    public int getTtwo2() {
        return ttwo2;
    }

    public String getCanc() {
        return canc;
    }
}
