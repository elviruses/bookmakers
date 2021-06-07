package org.example.bookmaker;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope("prototype")
public class Zenit extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(Zenit.class);

    private JSONObject json;
    private int reload;
    @Autowired
    private DB db;

    @Value("${zenit.url_json}")
    private String URL_JSON;
    @Value("${zenit.step}")
    private int STEP;
    @Value("${zenit.timeout}")
    public int TIMEOUT;
    @Value("${zenit.array_css}")
    public String[] CSS;
    @Value("${zenit.home_url}")
    public String HOME_URL;
    @Value("${zenit.dict}")
    private String DICT;
    @Value("${zenit.dict_team}")
    private String DICT_TEAM;
    @Value("${zenit.games}")
    private String GAMES;
    @Value("${zenit.dict_league}")
    private String DICT_LEAGUE;


    private final Map<Integer, String> league = new HashMap<>();
    private final Map<Integer, String> teams = new HashMap<>();
    private Map<String, Result> result = new HashMap<>();
    private final List<GameZenit> gameZenitList = new ArrayList<>();

    public JSONObject getJson(String url) throws IOException {
        return new JSONObject(Jsoup.connect(url).ignoreContentType(true).execute().body());
    }

    private int getCountMatch() throws IOException {
        Document page = Jsoup.parse(new URL(HOME_URL), TIMEOUT);
        return Integer.parseInt(page.select(CSS[0]).select(CSS[1]).select(CSS[2]).text());
    }

    @Override
    public void run() {
        try {
            int countMatch = getCountMatch();
            loadResult();
            for (int i = 0; i <= countMatch; i = i + STEP) {
                int length = Math.min((countMatch - i), STEP);
                String url = String.format(URL_JSON, i, length);
                logger.info("Вызываемый URL: {}", url);
                do {
                    try {
                        json = getJson(url);
                        reload = 0;
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                        reload++;
                    }
                } while (json == null && reload < 5);
                loadMapTeams();
                loadMapLeague();
                loadGame();
            }
            loadGameAndResult();
            save();
        } catch (Exception e) {
            Parser.setZenit_run(false);
            Parser.showMessage("Ошибка: " + e.getMessage());
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void loadMapTeams() {
        for (String key: json.getJSONObject(DICT).getJSONObject(DICT_TEAM).keySet()) {
            teams.put(Integer.parseInt(key), json.getJSONObject(DICT).getJSONObject(DICT_TEAM).get(key).toString());
        }
    }

    private void loadMapLeague() {
        for (String key: json.getJSONObject(DICT).getJSONObject(DICT_LEAGUE).keySet()) {
            league.put(Integer.parseInt(key), json.getJSONObject(DICT).getJSONObject(DICT_LEAGUE).get(key).toString());
        }
    }

    private void loadResult() {
        Result res = Parser.context.getBean("result", Result.class);
        res.loadResult();
        result = new HashMap<>(res.getMapResult());
    }

    private void loadGame() {
        for (String key: json.getJSONObject(GAMES).keySet()) {
            GameZenit game = Parser.context.getBean("gameZenit", GameZenit.class);
            gameZenitList.add(game.getObject(json.getJSONObject(GAMES).getJSONObject(key), this));
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

    public Map<Integer, String> getMapTeams() {
        return teams;
    }

    public Map<Integer, String> getMapLeague() {
        return league;
    }

    public List<GameZenit> getGameZenitList() {
        return gameZenitList;
    }

    private void save() {
        db.saveZenit(this);
    }
}
