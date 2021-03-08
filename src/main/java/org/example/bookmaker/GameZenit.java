package org.example.bookmaker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GameZenit {
    private LocalDateTime dateTimeMatch;
    private int team_one;
    private int team_two;
    private int id_match;
    private int id_league;
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

    public GameZenit(JSONObject json) {
        init(json);
    }

    public GameZenit(LocalDateTime dateTimeMatch, int id_match, int team_one, int team_two, int id_league,
                     double coeff_win1, double coeff_draw, double coeff_win2, double coeff_1x, double coeff_12,
                     double coeff_x2, double fora1, double coeff_fora1, double fora2, double coeff_fora2,
                     double coeff_total_min, double total, double coeff_total_max) {
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
    }

    private void init(JSONObject json) {
        String dateMatch = LocalDateTime.now().getYear() + "/" + json.get(Keys.DATE.get()).toString();
        dateTimeMatch = LocalDateTime.parse(dateMatch, DateTimeFormatter.ofPattern(Keys.DATE_PATTERN.get()));

        team_one = Integer.parseInt(json.get(Keys.TEAM_ONE.get()).toString());
        team_two = Integer.parseInt(json.get(Keys.TEAM_TWO.get()).toString());
        id_match = Integer.parseInt(json.get(Keys.MATCH_ID.get()).toString());
        id_league = Integer.parseInt(json.get(Keys.LEAGUE_ID.get()).toString());

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
    public int hashCode() {
        int primeNumber = 31;
        return (int) (primeNumber + this.dateTimeMatch.hashCode() + this.team_one + this.team_two + this.id_match +
                      this.id_league + this.coeff_win1 + this.coeff_draw + this.coeff_win2 + this.coeff_1x +
                      this.coeff_12 + this.coeff_x2 + this.fora1 + this.coeff_fora1 + this.fora2 + this.coeff_fora2 +
                      this.coeff_total_min + this.total + this.coeff_total_max);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!(obj instanceof GameZenit))
            return false;

        GameZenit model = (GameZenit) obj;

        return this.dateTimeMatch.equals(model.getDateTimeMatch()) && this.team_one == model.getTeam_one() &&
                this.team_two == model.getTeam_two() && this.id_match == model.getId_match() &&
                this.id_league == model.getId_league() && this.coeff_win1 == model.getCoeff_win1() &&
                this.coeff_draw == model.getCoeff_draw() && this.coeff_win2 == model.getCoeff_win2() &&
                this.coeff_1x == model.getCoeff_1x() && this.coeff_12 == model.getCoeff_12() &&
                this.coeff_x2 == model.getCoeff_x2() && this.fora1 == model.getFora1() &&
                this.coeff_fora1 == model.getCoeff_fora1() && this.fora2 == model.getFora2() &&
                this.coeff_fora2 == model.getCoeff_fora2() && this.coeff_total_min == model.getCoeff_total_min() &&
                this.total == model.getTotal() && this.coeff_total_max == model.getCoeff_total_max();
    }

    public LocalDateTime getDateTimeMatch() {
        return dateTimeMatch;
    }

    public int getTeam_one() {
        return team_one;
    }

    public int getTeam_two() {
        return team_two;
    }

    public int getId_match() {
        return id_match;
    }

    public int getId_league() {
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
}
