package org.example.bookmaker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class GameZenit {
    private Zenit zenit;

    private LocalDateTime dateTimeMatch;
    private String team_one;
    private String team_two;
    private int id_match;
    private String id_league;
    private double coeff_win1 = -99.0d;
    private double coeff_draw = -99.0d;
    private double coeff_win2 = -99.0d;
    private double coeff_1x = -99.0d;
    private double coeff_12 = -99.0d;
    private double coeff_x2 = -99.0d;
    private double fora1 = -99.0d;
    private double coeff_fora1 = -99.0d;
    private double fora2 = -99.0d;
    private double coeff_fora2 = -99.0d;
    private double coeff_total_min = -99.0d;
    private double total = -99.0d;
    private double coeff_total_max = -99.0d;
    private int scde = 0;
    private int tone1 = -99;
    private int tone2 = -99;
    private int ttwo1 = -99;
    private int ttwo2 = -99;
    private String canc = "-";

    public GameZenit(JSONObject json, Zenit zenit) {
        this.zenit = zenit;
        init(json);
    }

    public GameZenit(LocalDateTime dateTimeMatch, String team_one, String team_two, int id_match,
                     String id_league, double coeff_win1, double coeff_draw, double coeff_win2,
                     double coeff_1x, double coeff_12, double coeff_x2, double fora1, double coeff_fora1,
                     double fora2, double coeff_fora2, double coeff_total_min, double total,
                     double coeff_total_max, int tone1, int tone2, int ttwo1, int ttwo2, String canc) {
        this.dateTimeMatch = dateTimeMatch;
        this.team_one = team_one;
        this.team_two = team_two;
        this.id_match = id_match;
        this.id_league = id_league;
        this.coeff_win1 = coeff_win1;
        this.coeff_draw = coeff_draw;
        this.coeff_win2 = coeff_win2;
        this.coeff_1x = coeff_1x;
        this.coeff_12 = coeff_12;
        this.coeff_x2 = coeff_x2;
        this.fora1 = fora1;
        this.coeff_fora1 = coeff_fora1;
        this.fora2 = fora2;
        this.coeff_fora2 = coeff_fora2;
        this.coeff_total_min = coeff_total_min;
        this.total = total;
        this.coeff_total_max = coeff_total_max;
        this.tone1 = tone1;
        this.tone2 = tone2;
        this.ttwo1 = ttwo1;
        this.ttwo2 = ttwo2;
        this.canc = canc;
    }

    private void init(JSONObject json) {
        String dateMatch = LocalDateTime.now().getYear() + "/" + json.get(Keys.DATE.get()).toString();
        dateTimeMatch = LocalDateTime.parse(dateMatch, DateTimeFormatter.ofPattern(Keys.DATE_PATTERN.get()));

        team_one = zenit.getMapTeams().get(Integer.parseInt(json.get(Keys.TEAM_ONE.get()).toString()));
        team_two = zenit.getMapTeams().get(Integer.parseInt(json.get(Keys.TEAM_TWO.get()).toString()));
        id_match = Integer.parseInt(json.get(Keys.MATCH_ID.get()).toString());
        id_league = zenit.getMapLeague().get(Integer.parseInt(json.get(Keys.LEAGUE_ID.get()).toString()));

        JSONArray jsonArray = json.getJSONArray(Keys.COEFF_ARRAY.get());
        if (jsonArray.length() == 13) {
            for (int i = 1; i <= jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i-1);
                switch (i) {
                    case 1: coeff_win1 = jsonObject.has(Keys.COEFF_VALUE.get()) ? Double.parseDouble(jsonObject.get(Keys.COEFF_VALUE.get()).toString()) : -99; break;
                    case 2: coeff_draw = jsonObject.has(Keys.COEFF_VALUE.get()) ? Double.parseDouble(jsonObject.get(Keys.COEFF_VALUE.get()).toString()) : -99; break;
                    case 3: coeff_win2 = jsonObject.has(Keys.COEFF_VALUE.get()) ? Double.parseDouble(jsonObject.get(Keys.COEFF_VALUE.get()).toString()) : -99; break;
                    case 4: coeff_1x = jsonObject.has(Keys.COEFF_VALUE.get()) ? Double.parseDouble(jsonObject.get(Keys.COEFF_VALUE.get()).toString()) : -99; break;
                    case 5: coeff_12 = jsonObject.has(Keys.COEFF_VALUE.get()) ? Double.parseDouble(jsonObject.get(Keys.COEFF_VALUE.get()).toString()) : -99; break;
                    case 6: coeff_x2 = jsonObject.has(Keys.COEFF_VALUE.get()) ? Double.parseDouble(jsonObject.get(Keys.COEFF_VALUE.get()).toString()) : -99; break;
                    case 7: fora1 =  jsonObject.has(Keys.COEFF_VALUE.get()) ? Double.parseDouble(jsonObject.get(Keys.COEFF_VALUE.get()).toString()) : -99; break;
                    case 8: coeff_fora1 = jsonObject.has(Keys.COEFF_VALUE.get()) ? Double.parseDouble(jsonObject.get(Keys.COEFF_VALUE.get()).toString()) : -99; break;
                    case 9: fora2 = jsonObject.has(Keys.COEFF_VALUE.get()) ? Double.parseDouble(jsonObject.get(Keys.COEFF_VALUE.get()).toString()) : -99; break;
                    case 10: coeff_fora2 = jsonObject.has(Keys.COEFF_VALUE.get()) ? Double.parseDouble(jsonObject.get(Keys.COEFF_VALUE.get()).toString()) : -99; break;
                    case 11: coeff_total_min = jsonObject.has(Keys.COEFF_VALUE.get()) ? Double.parseDouble(jsonObject.get(Keys.COEFF_VALUE.get()).toString()) : -99; break;
                    case 12: total = jsonObject.has(Keys.COEFF_VALUE.get()) ? Double.parseDouble(jsonObject.get(Keys.COEFF_VALUE.get()).toString()) : -99; break;
                    case 13: coeff_total_max = jsonObject.has(Keys.COEFF_VALUE.get()) ? Double.parseDouble(jsonObject.get(Keys.COEFF_VALUE.get()).toString()) : -99; break;
                }
            }
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameZenit gameZenit = (GameZenit) o;
        return id_match == gameZenit.getId_match() &&
                Double.compare(gameZenit.getCoeff_win1(), coeff_win1) == 0 &&
                Double.compare(gameZenit.getCoeff_draw(), coeff_draw) == 0 &&
                Double.compare(gameZenit.getCoeff_win2(), coeff_win2) == 0 &&
                Double.compare(gameZenit.getCoeff_1x(), coeff_1x) == 0 &&
                Double.compare(gameZenit.getCoeff_12(), coeff_12) == 0 &&
                Double.compare(gameZenit.getCoeff_x2(), coeff_x2) == 0 &&
                Double.compare(gameZenit.getFora1(), fora1) == 0 &&
                Double.compare(gameZenit.getCoeff_fora1(), coeff_fora1) == 0 &&
                Double.compare(gameZenit.getFora2(), fora2) == 0 &&
                Double.compare(gameZenit.getCoeff_fora2(), coeff_fora2) == 0 &&
                Double.compare(gameZenit.getCoeff_total_min(), coeff_total_min) == 0 &&
                Double.compare(gameZenit.getTotal(), total) == 0 &&
                Double.compare(gameZenit.getCoeff_total_max(), coeff_total_max) == 0 &&
                tone1 == gameZenit.getTone1() &&
                tone2 == gameZenit.getTone2() &&
                ttwo1 == gameZenit.getTtwo1() &&
                ttwo2 == gameZenit.getTtwo2() &&
                Objects.equals(dateTimeMatch, gameZenit.getDateTimeMatch()) &&
                Objects.equals(team_one, gameZenit.getTeam_one()) &&
                Objects.equals(team_two, gameZenit.getTeam_two()) &&
                Objects.equals(id_league, gameZenit.getId_league()) &&
                Objects.equals(canc, gameZenit.getCanc());
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTimeMatch, team_one, team_two, id_match, id_league, coeff_win1,
                coeff_draw, coeff_win2, coeff_1x, coeff_12, coeff_x2, fora1, coeff_fora1, fora2,
                coeff_fora2, coeff_total_min, total, coeff_total_max, tone1, tone2, ttwo1, ttwo2, canc);
    }

    @Override
    public String toString() {
        return "GameZenit{" +
                "dateTimeMatch=" + dateTimeMatch +
                ", team_one='" + team_one + '\'' +
                ", team_two='" + team_two + '\'' +
                ", id_match=" + id_match +
                ", id_league='" + id_league + '\'' +
                ", coeff_win1=" + coeff_win1 +
                ", coeff_draw=" + coeff_draw +
                ", coeff_win2=" + coeff_win2 +
                ", coeff_1x=" + coeff_1x +
                ", coeff_12=" + coeff_12 +
                ", coeff_x2=" + coeff_x2 +
                ", fora1=" + fora1 +
                ", coeff_fora1=" + coeff_fora1 +
                ", fora2=" + fora2 +
                ", coeff_fora2=" + coeff_fora2 +
                ", coeff_total_min=" + coeff_total_min +
                ", total=" + total +
                ", coeff_total_max=" + coeff_total_max +
                ", scde=" + scde +
                ", tone1=" + tone1 +
                ", tone2=" + tone2 +
                ", ttwo1=" + ttwo1 +
                ", ttwo2=" + ttwo2 +
                ", canc='" + canc + '\'' +
                '}';
    }

    public LocalDateTime getDateTimeMatch() {
        return dateTimeMatch;
    }

    public String getTeam_one() {
        return team_one;
    }

    public String getTeam_two() {
        return team_two;
    }

    public int getId_match() {
        return id_match;
    }

    public String getId_league() {
        return id_league;
    }

    public double getCoeff_win1() {
        return coeff_win1;
    }

    public double getCoeff_draw() {
        return coeff_draw;
    }

    public double getCoeff_win2() {
        return coeff_win2;
    }

    public double getFora1() {
        return fora1;
    }

    public double getCoeff_fora1() {
        return coeff_fora1;
    }

    public double getFora2() {
        return fora2;
    }

    public double getCoeff_fora2() {
        return coeff_fora2;
    }

    public double getCoeff_total_min() {
        return coeff_total_min;
    }

    public double getTotal() {
        return total;
    }

    public double getCoeff_total_max() {
        return coeff_total_max;
    }

    public double getCoeff_1x() {
        return coeff_1x;
    }

    public double getCoeff_12() {
        return coeff_12;
    }

    public double getCoeff_x2() {
        return coeff_x2;
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

    public int getScde() {
        return scde;
    }

    public void setScde(int scde) {
        this.scde = scde;
    }

    public void setTone1(int tone1) {
        this.tone1 = tone1;
    }

    public void setTone2(int tone2) {
        this.tone2 = tone2;
    }

    public void setTtwo1(int ttwo1) {
        this.ttwo1 = ttwo1;
    }

    public void setTtwo2(int ttwo2) {
        this.ttwo2 = ttwo2;
    }

    public void setCanc(String canc) {
        this.canc = canc;
    }
}
