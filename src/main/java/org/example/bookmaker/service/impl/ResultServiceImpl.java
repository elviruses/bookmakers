package org.example.bookmaker.service.impl;

import lombok.Data;
import org.example.bookmaker.Result;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ResultServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(Result.class);

    private JSONObject json;
    private int reload;

    private final String URL_JSON = "https://zenit.win/nws/v1/matchesresults?timezone_id=3&is_live=0&main_score=0&date_from=%s&date_to=%s&site_type=1&language_id=1049&sports%%5B0%%5D=1&include_other=0";
    private final String DATE_PATTERN = "yyyy/dd/MM HH:mm";

    private LocalDateTime dateTimeMatch;
    private String teamOne;
    private String teamTwo;
    private int tone1;
    private int tone2;
    private int ttwo1;
    private int ttwo2;
    private String canc;
    private final List<ResultServiceImpl> mapResult = new ArrayList<>();

    public ResultServiceImpl(ResultServiceImpl result) {
        this.dateTimeMatch = result.getDateTimeMatch();
        this.teamOne = result.getTeamOne();
        this.teamTwo = result.getTeamTwo();
        this.tone1 = result.getTone1();
        this.tone2 = result.getTone2();
        this.ttwo1 = result.getTtwo1();
        this.ttwo2 = result.getTtwo2();
        this.canc = result.getCanc();
    }

    public void loadResult() {
        try {
            String fromDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-M-d"));
            String toDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-M-d"));
            String url = String.format(URL_JSON, fromDate, toDate);
            logger.info("URL получения результатов: {}", url);
            do {
                try {
                    json = getJson(url);
                    reload = 0;
                } catch (IOException e) {
                    logger.error("Ошибка получения JSON результатов, попытка: {}", reload);
                    logger.error(e.getMessage(), e);
                    reload++;
                }
            } while (json == null && reload < 5);

            if (!json.has("code") && !json.get("code").equals("ERROR")) {
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
        for (String key: json.getJSONObject("result").getJSONObject("games").keySet()) {
            String[] result = replacer(json.getJSONObject("result").getJSONObject("games").getJSONObject(key).get("score").toString());
            if (result.length == 0 || result.length == 1) {
                tone1 = -99;
                tone2 = -99;
                ttwo1 = -99;
                ttwo2 = -99;
                canc = (json.getJSONObject("result").has("cancel") &&
                        json.getJSONObject("result").getJSONObject("cancel").has(key))
                        ? json.getJSONObject("result").getJSONObject("cancel").get(key).toString()
                        : "-";
            } else if (result.length == 2) {
                tone1 = -99;
                tone2 = -99;
                ttwo1 = Integer.parseInt(result[0]);
                ttwo2 = Integer.parseInt(result[1]);
                canc = (json.getJSONObject("result").has("cancel") &&
                        json.getJSONObject("result").getJSONObject("cancel").has(key))
                        ? json.getJSONObject("result").getJSONObject("cancel").get(key).toString()
                        : "-";
            } else {
                tone1 = Integer.parseInt(result[2]);
                tone2 = Integer.parseInt(result[3]);
                ttwo1 = Integer.parseInt(result[0]);
                ttwo2 = Integer.parseInt(result[1]);
                canc = (json.getJSONObject("result").has("cancel") &&
                        json.getJSONObject("result").getJSONObject("cancel").has(key))
                        ? json.getJSONObject("result").getJSONObject("cancel").get(key).toString()
                        : "-";
            }

            String dateMatch = LocalDateTime.now().getYear() + "/" + json.getJSONObject("result").getJSONObject("games").getJSONObject(key).get("date").toString();
            dateTimeMatch = LocalDateTime.parse(dateMatch, DateTimeFormatter.ofPattern(DATE_PATTERN));
            teamOne = json.getJSONObject("result").getJSONObject("games").getJSONObject(key).get("team_1_name").toString();
            teamTwo = json.getJSONObject("result").getJSONObject("games").getJSONObject(key).get("team_2_name").toString();
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
}
