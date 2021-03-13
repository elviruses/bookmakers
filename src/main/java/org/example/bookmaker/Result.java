package org.example.bookmaker;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Result {
    private static final Logger logger = LoggerFactory.getLogger(Result.class);

    private JSONObject json;
    private int reload;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
    private final String URL_JSON_RESULT = "https://zenit.win/nws/v1/matchesresults?timezone_id=3&is_live=0&main_score=0&date_from=%s&date_to=%s&site_type=1&language_id=1049&sports%%5B0%%5D=1&include_other=0";

    private LocalDateTime dateTimeMatch;
    private String teamOne;
    private String teamTwo;
    private int tone1 = -99;
    private int tone2 = -99;
    private int ttwo1 = -99;
    private int ttwo2 = -99;
    private String canc = "-";
    private Map<String, Result> mapResult = new HashMap<>();

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
            String nowDate = LocalDate.now().format(formatter);
            String url = String.format(URL_JSON_RESULT, nowDate, nowDate);
            logger.info("URL получения результатов: {}", url);
            do {
                try {
                    json = getJson(url);
                    reload = 0;
                } catch (IOException e) {
                    logger.info("Ошибка получения JSON результатов, попытка: {}", reload);
                    logger.error(e.getMessage(), e);
                    reload++;
                }
            } while (json == null && reload < 5);
            if (json.has("code") && json.get("code").equals("ERROR")) {

            } else {
                loadMapResult();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public JSONObject getJson(String url) throws IOException {
        return new JSONObject(Jsoup.connect(url).ignoreContentType(true).execute().body());
    }

    private void loadMapResult() {
        for (String key: json.getJSONObject(Keys.RESULT.get()).getJSONObject(Keys.GAMES.get()).keySet()) {
            String[] result = replacer(json.getJSONObject(Keys.RESULT.get()).getJSONObject(Keys.GAMES.get()).getJSONObject(key).get(Keys.SCORE.get()).toString());
            if (result.length == 0) {
                tone1 = -99;
                tone2 = -99;
                ttwo1 = -99;
                ttwo2 = -99;
                canc = (json.getJSONObject(Keys.RESULT.get()).has(Keys.CANCEL.get()) &&
                        json.getJSONObject(Keys.RESULT.get()).getJSONObject(Keys.CANCEL.get()).has(key))
                        ? json.getJSONObject(Keys.RESULT.get()).getJSONObject(Keys.CANCEL.get()).get(key).toString()
                        : "-";
            } else if (result.length == 2) {
                tone1 = -99;
                tone2 = -99;
                ttwo1 = Integer.parseInt(result[0]);
                ttwo2 = Integer.parseInt(result[1]);
                canc = (json.getJSONObject(Keys.RESULT.get()).has(Keys.CANCEL.get()) &&
                        json.getJSONObject(Keys.RESULT.get()).getJSONObject(Keys.CANCEL.get()).has(key))
                        ? json.getJSONObject(Keys.RESULT.get()).getJSONObject(Keys.CANCEL.get()).get(key).toString()
                        : "-";
            } else {
                tone1 = Integer.parseInt(result[2]);
                tone2 = Integer.parseInt(result[3]);
                ttwo1 = Integer.parseInt(result[0]);
                ttwo2 = Integer.parseInt(result[1]);
                canc = (json.getJSONObject(Keys.RESULT.get()).has(Keys.CANCEL.get()) &&
                        json.getJSONObject(Keys.RESULT.get()).getJSONObject(Keys.CANCEL.get()).has(key))
                        ? json.getJSONObject(Keys.RESULT.get()).getJSONObject(Keys.CANCEL.get()).get(key).toString()
                        : "-";
            }

            String dateMatch = LocalDateTime.now().getYear() + "/" + json.getJSONObject(Keys.RESULT.get()).getJSONObject(Keys.GAMES.get()).getJSONObject(key).get("date").toString();
            dateTimeMatch = LocalDateTime.parse(dateMatch, DateTimeFormatter.ofPattern(Keys.DATE_PATTERN.get()));
            teamOne = json.getJSONObject(Keys.RESULT.get()).getJSONObject(Keys.GAMES.get()).getJSONObject(key).get("team_1_name").toString();
            teamTwo = json.getJSONObject(Keys.RESULT.get()).getJSONObject(Keys.GAMES.get()).getJSONObject(key).get("team_2_name").toString();
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
