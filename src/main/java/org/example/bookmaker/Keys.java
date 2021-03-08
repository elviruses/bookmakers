package org.example.bookmaker;

public enum Keys {
    DICT ("dict"),
    DICT_LEAGUE ("league"),
    DICT_TEAM ("cmd"),
    GAMES ("games"),
    RESULT ("result"),
    SCORE ("score"),
    CANCEL ("cancel"),
    DATE ("date"),
    TEAM_ONE  ("c1_id"),
    TEAM_TWO  ("c2_id"),
    MATCH_ID  ("id"),
    LEAGUE_ID  ("lid"),
    COEFF_ARRAY  ("f_l"),
    COEFF_VALUE  ("h"),
    DATE_PATTERN ("yyyy/dd/MM HH:mm"),
    HOME_URL ("https://zenit.win/line/football");

    private String key;

    Keys(String key) {
        this.key = key;
    }

    public String get() {
        return key;
    }

    @Override
    public String toString() {
        return key;
    }
}
