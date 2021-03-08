package org.example.bookmaker;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component("resultBean")
public class Result extends Thread {
    private JSONObject json;
    private int reload;
    private String nowDate;
    private String url;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");

    private final String URL_JSON_RESULT = "https://zenit.win/ajax/results/get?timezone=3&type=0&main_score=0&date%%5Bfrom%%5D=%s&date%%5Bto%%5D=%s&sport%%5B0%%5D=1";
    private final int SLEEP_MIN = 300;

    private int tone1;
    private int tone2;
    private int ttwo1;
    private int ttwo2;
    private String canc;
    private Map<Integer, Result> mapResult;

    @Override
    public void run() {
        try {
            while (true) {
                nowDate = LocalDate.now().format(formatter);
                url = String.format(URL_JSON_RESULT, nowDate, nowDate);
                System.out.println(url);
                do {
                    try {
                        json = getJson(url);
                        reload = 0;
                    } catch (IOException e) {
                        reload++;
                        /*NIO*/
                    }
                } while (json == null && reload < 5);
                if (json.has("code") && json.get("code").equals("ERROR")) {

                } else {
                    loadMapResult();
                    save();
                }
                Thread.sleep(SLEEP_MIN * 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject getJson(String url) throws IOException {
        return new JSONObject(Jsoup.connect(url).ignoreContentType(true).execute().body());
    }

    private void loadMapResult() {
        mapResult = new HashMap<>();
        for (String key: json.getJSONObject(Keys.RESULT.get()).getJSONObject(Keys.GAMES.get()).keySet()) {
            String[] result = replacer(json.getJSONObject(Keys.RESULT.get()).getJSONObject(Keys.GAMES.get()).getJSONObject(key).get(Keys.SCORE.get()).toString());
            System.out.println(Arrays.toString(result));
            if (result.length == 0)
                continue;
            if (result.length == 2) {
                tone1 = -99;
                tone2 = -99;
                ttwo1 = Integer.parseInt(result[0]);
                ttwo2 = Integer.parseInt(result[1]);
                canc = null;
            } else {
                tone1 = Integer.parseInt(result[2]);
                tone2 = Integer.parseInt(result[3]);
                ttwo1 = Integer.parseInt(result[0]);
                ttwo2 = Integer.parseInt(result[1]);
                canc = null;
            }
            mapResult.put(Integer.parseInt(key), this);
        }

        if (json.getJSONObject(Keys.RESULT.get()).has(Keys.CANCEL.get())) {
            for (String key: json.getJSONObject(Keys.RESULT.get()).getJSONObject(Keys.CANCEL.get()).keySet()) {
                String textCancel = json.getJSONObject(Keys.RESULT.get()).getJSONObject(Keys.CANCEL.get()).get(key).toString();
                tone1 = -99;
                tone2 = -99;
                ttwo1 = -99;
                ttwo2 = -99;
                canc = textCancel;
                mapResult.put(Integer.parseInt(key), this);
            }
        }
    }

    private String[] replacer(String string) {
        return string.replace(":", " ")
                     .replace("(", "")
                     .replace(")", "")
                     .split(" ");
    }

    private void save() {
        DB.getInstance().saveResult(mapResult);
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
