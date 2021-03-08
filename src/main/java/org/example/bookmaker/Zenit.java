package org.example.bookmaker;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component("zenitBean")
@Scope("prototype")
public class Zenit extends Thread {
    private JSONObject json;
    private int reload;
    private String type;
    private int countMatch;

    private static final String URL_JSON = "https://zenit.win/ajax/line/printer/react?all=0&onlyview=0&timeline=0&sport=1&league=&games=&ross=0&popular_group=&offset=%d&length=%d&timezone=3&lang_id=1&sort_mode=2&b_id=&popular=0&client_v=";
    private static final int STEP = 400;
    private static final int SLEEP_MAX = 3600;
    private static final int SLEEP_MIN = 300;
    private static final int TIMEOUT = 15000;

    private Map<Integer, String> league = new HashMap<>();
    private Map<Integer, String> teams = new HashMap<>();
    private ArrayList<GameZenit> gameZenitArrayList = new ArrayList<>();

    public void setType(String type) {
        this.type = type;
    }

    private JSONObject getJson(String url) throws IOException {
        return new JSONObject(Jsoup.connect(url).ignoreContentType(true).execute().body());
    }

    private int getCountMatch() throws IOException {
        Document page = Jsoup.parse(new URL(Keys.HOME_URL.get()), TIMEOUT);
        return Integer.parseInt(page.select("div[class=favourites-sport-item__inner]").select("div[data-tip=Футбол]").select("div[class=favourites-sport-item-count]").text());
    }

    public static int getSleepMin() {
        return SLEEP_MIN;
    }

    @Override
    public void run() {
        try {
            if (type.equals("general")) {
                while (true) {
                    countMatch = getCountMatch();
                    for (int i=0; i <= countMatch; i = i + STEP) {
                        int length = Math.min((countMatch - i), STEP);
                        String url = String.format(URL_JSON, i, length);
                        System.out.println(type + " : " + url);
                        do {
                            try {
                                json = getJson(url);
                                reload = 0;
                            } catch (IOException e) {
                                reload++;
                                /*NIO*/
                            }
                        } while (json == null && reload < 5);
                        loadMapTeams();
                        loadMapLeague();
                        loadGame();
                        save();
                    }
                    Thread.sleep(SLEEP_MAX * 1000);
                }
            } else if (type.equals("second")) {
                while (true) {
                    if (DB.getInstance().getReadyMatch() > 0) {
                        countMatch = getCountMatch();
                        for (int i=0; i <= countMatch; i = i + STEP) {
                            int length = Math.min((countMatch - i), STEP);
                            String url = String.format(URL_JSON, i, length);
                            System.out.println(type + " : " + url);
                            do {
                                try {
                                    json = getJson(url);
                                    reload = 0;
                                } catch (IOException e) {
                                    reload++;
                                    /*NIO*/
                                }
                            } while (json == null && reload < 5);
                            loadMapTeams();
                            loadMapLeague();
                            loadGame();
                            save();
                        }
                    }
                    Thread.sleep(SLEEP_MIN * 1000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMapTeams() {
        teams = new HashMap<>();
        for (String key: json.getJSONObject(Keys.DICT.get()).getJSONObject(Keys.DICT_TEAM.get()).keySet()) {
            teams.put(Integer.parseInt(key), json.getJSONObject(Keys.DICT.get()).getJSONObject(Keys.DICT_TEAM.get()).get(key).toString());
        }
    }

    private void loadMapLeague() {
        league = new HashMap<>();
        for (String key: json.getJSONObject(Keys.DICT.get()).getJSONObject(Keys.DICT_LEAGUE.get()).keySet()) {
            league.put(Integer.parseInt(key), json.getJSONObject(Keys.DICT.get()).getJSONObject(Keys.DICT_LEAGUE.get()).get(key).toString());
        }
    }

    private void loadGame() {
        gameZenitArrayList = new ArrayList<>();
        for (String key: json.getJSONObject(Keys.GAMES.get()).keySet()) {
            gameZenitArrayList.add(new GameZenit((JSONObject) json.getJSONObject(Keys.GAMES.get()).get(key)));
        }
    }

    public Map<Integer, String> getMapTeams() {
        return teams;
    }

    public Map<Integer, String> getMapLeague() {
        return league;
    }

    public ArrayList<GameZenit> getGameZenitArrayList() {
        return gameZenitArrayList;
    }

    private void save() {
        DB.getInstance().saveZenit(this);
    }
}
