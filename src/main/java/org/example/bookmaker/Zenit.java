package org.example.bookmaker;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Zenit extends Thread {
    private JSONObject json;
    private int reload;
    private int countMatch;

    private static final String URL_JSON = "https://zenit.win/ajax/line/printer/react?all=0&onlyview=0&timeline=0&sport=1&league=&games=&ross=0&popular_group=&offset=%d&length=%d&timezone=3&lang_id=1&sort_mode=2&b_id=&popular=0&client_v=";
    private static final int STEP = 400;
    private static final int TIMEOUT = 15000;

    private Map<Integer, String> league = new HashMap<>();
    private Map<Integer, String> teams = new HashMap<>();
    private Map<String, Result> result = new HashMap<>();
    private ArrayList<GameZenit> gameZenitList = new ArrayList<>();

    private JSONObject getJson(String url) throws IOException {
        return new JSONObject(Jsoup.connect(url).ignoreContentType(true).execute().body());
    }

    private int getCountMatch() throws IOException {
        Document page = Jsoup.parse(new URL(Keys.HOME_URL.get()), TIMEOUT);
        return Integer.parseInt(page.select("div[class=favourites-sport-item__inner]").select("div[data-tip=Футбол]").select("div[class=favourites-sport-item-count]").text());
    }

    @Override
    public void run() {
        try {
            countMatch = getCountMatch();
            loadResult();
            for (int i=0; i <= countMatch; i = i + STEP) {
                int length = Math.min((countMatch - i), STEP);
                String url = String.format(URL_JSON, i, length);
                System.out.println(url);
                do {
                    try {
                        json = getJson(url);
                        reload = 0;
                    } catch (IOException e) {
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
            e.printStackTrace();
        }
    }

    private void loadMapTeams() {
        for (String key: json.getJSONObject(Keys.DICT.get()).getJSONObject(Keys.DICT_TEAM.get()).keySet()) {
            teams.put(Integer.parseInt(key), json.getJSONObject(Keys.DICT.get()).getJSONObject(Keys.DICT_TEAM.get()).get(key).toString());
        }
    }

    private void loadMapLeague() {
        for (String key: json.getJSONObject(Keys.DICT.get()).getJSONObject(Keys.DICT_LEAGUE.get()).keySet()) {
            league.put(Integer.parseInt(key), json.getJSONObject(Keys.DICT.get()).getJSONObject(Keys.DICT_LEAGUE.get()).get(key).toString());
        }
    }

    private void loadResult() {
        result = new HashMap<>((new Result()).getMapResult());
    }

    private void loadGame() {
        for (String key: json.getJSONObject(Keys.GAMES.get()).keySet()) {
            gameZenitList.add(new GameZenit(json.getJSONObject(Keys.GAMES.get()).getJSONObject(key), this));
        }
    }

    private void loadGameAndResult() {
        ArrayList<GameZenit> gameZenitInDB = new ArrayList<>(DB.getInstance().getGameZenitInDB());

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
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("log.txt", true))) {
                    writer.write("Добавление в резы: " + game + '\n');
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Map<Integer, String> getMapTeams() {
        return teams;
    }

    public Map<Integer, String> getMapLeague() {
        return league;
    }

    public ArrayList<GameZenit> getGameZenitList() {
        return gameZenitList;
    }

    public Map<String, Result> getResult() {
        return result;
    }

    private void save() {
        DB.getInstance().saveZenit(this);
    }


}
