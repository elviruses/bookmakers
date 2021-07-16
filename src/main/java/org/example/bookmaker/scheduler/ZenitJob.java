package org.example.bookmaker.scheduler;

import org.example.bookmaker.GameZenit;
import org.example.bookmaker.Parser;
import org.example.bookmaker.Result;
import org.example.bookmaker.Zenit;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ZenitJob {
    private static final Logger log = LoggerFactory.getLogger(ZenitJob.class);

    @Value("${zenit.step}")
    private int STEP;
    @Value("${zenit.timeout}")
    public int TIMEOUT;

    private JSONObject json;
    private int reload;
    private final String URL_JSON = "https://zenit.win/ajax/line/printer/react?all=0&onlyview=0&timeline=0&sport=1&league=&games=&ross=0&popular_group=&offset=%d&length=%d&timezone=3&lang_id=1&sort_mode=2&b_id=&popular=0&client_v=";
    public final String[] CSS = {"div[class=favourites-sport-item__inner]","div[data-tip=Футбол]","div[class=favourites-sport-item-count]"};
    private final String HOME_URL = "https://zenit.win/line/football";

    private final Map<Integer, String> league = new HashMap<>();
    private final Map<Integer, String> teams = new HashMap<>();
    private final Map<String, Result> result = new HashMap<>();
    private final List<GameZenit> gameZenitList = new ArrayList<>();

    @Scheduled(fixedDelay = 60000)
    public void run() {
        result.clear();
        teams.clear();
        league.clear();

        loadGameFromJson();
        loadResult();
        loadGameAndResult();
        save();
    }

    private void loadGameFromJson() {
        try {
            int countMatch = getCountMatch();
            for (int i = 0; i <= countMatch; i = i + STEP) {
                int length = Math.min((countMatch - i), STEP);
                String url = String.format(URL_JSON, i, length);
                log.info("Вызываемый URL: {}", url);
                do {
                    try {
                        json = getJson(url);
                        reload = 0;
                    } catch (IOException e) {
                        log.error("Ошибка получения JSON игр, попытка: {}", reload);
                        log.error(e.getMessage(), e);
                        reload++;
                    }
                } while (json == null && reload < 5);
                loadMapTeams();
                loadMapLeague();
                loadGame();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    private int getCountMatch() throws IOException {
        Document page = Jsoup.parse(new URL(HOME_URL), TIMEOUT);
        return Integer.parseInt(page.select(CSS[0]).select(CSS[1]).select(CSS[2]).text());
    }

    private void loadResult() {
        Result res = Parser.context.getBean("result", Result.class);
        res.loadResult();
        result = new HashMap<>(res.getMapResult());
    }

    public JSONObject getJson(String url) throws IOException {
        return new JSONObject(Jsoup.connect(url).ignoreContentType(true).execute().body());
    }

    private void loadMapTeams() {
        for (String key: json.getJSONObject("dict").getJSONObject("cmd").keySet()) {
            teams.put(Integer.parseInt(key), json.getJSONObject("dict").getJSONObject("cmd").get(key).toString());
        }
    }

    private void loadMapLeague() {
        for (String key: json.getJSONObject("dict").getJSONObject("league").keySet()) {
            league.put(Integer.parseInt(key), json.getJSONObject("dict").getJSONObject("league").get(key).toString());
        }
    }

    private void loadGame() {
        gameZenitList
        for (String key: json.getJSONObject("games").keySet()) {
            GameZenit game = Parser.context.getBean("gameZenit", GameZenit.class);
            gameZenitList.add(game.getObject(json.getJSONObject("games").getJSONObject(key), this));
        }
    }

    private void loadGameAndResult() {
        ArrayList<GameZenit> gameZenitInDB = new ArrayList<>(db.getGameZenitInDB());

        for (GameZenit game: gameZenitInDB) {
            String keyInMapResult = game.getDateTimeMatch() + game.getTeam_one() + game.getTeam_two();
            if (result.containsKey(keyInMapResult)) {
                game.setScde(1);
                game.setTone1(result.get(keyInMapResult).getTone1());
                game.setTone2(result.get(keyInMapResult).getTone2());
                game.setTtwo1(result.get(keyInMapResult).getTtwo1());
                game.setTtwo2(result.get(keyInMapResult).getTtwo2());
                game.setCanc(result.get(keyInMapResult).getCanc());
                gameZenitList.add(game);
                logger.info("Добавление результатов: {}", game);
            }
        }
    }

    private void save() {
        db.saveZenit(this);
    }
}
