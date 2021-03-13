package org.example.bookmaker;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DB {

    private static final String URL = "jdbc:postgresql://localhost:5432/first_db";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "1";
    private static final String PATTERN_SAVE_DATE = "dd.MM.yyyy HH:mm:ss";
    private static final String PATTERN_SAVE_DATE_DB = "dd.mm.yyyy hh24:mi:ss";
    private static final String SQL_FOR_READY_MATCH = "SELECT count(*) as c FROM (SELECT extract(epoch FROM age(b.oddt, LOCALTIMESTAMP))/60 tim FROM games_zenit b JOIN (SELECT idmt, max(id) idmax FROM games_zenit WHERE idmt NOT IN (SELECT idmt FROM games_zenit WHERE scde=1) group by idmt) t on t.idmax = b.id WHERE b.oddt > LOCALTIMESTAMP) tab WHERE tab.tim between 4 and 8";

    private static Connection connection;
    private static DB instance;

    private DB() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static DB getInstance() {
        if (instance == null)
            instance = new DB();
        return instance;
    }

    public void saveZenit(Zenit zenit) {
        saveGameZenit(zenit.getGameZenitList());
    }

    public int getReadyMatch() {
        int result = 0;
        try (Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery(SQL_FOR_READY_MATCH)) {
            while (resultSet.next()) {
                result = resultSet.getInt("c");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<GameZenit> getGameZenitInDB() {
        ArrayList<GameZenit> gameZenitInDB = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery("SELECT b.* FROM games_zenit b join (SELECT idmt, max(id) idmax FROM games_zenit WHERE idmt NOT IN (SELECT idmt FROM games_zenit WHERE scde=1) group by idmt) t on t.idmax = b.id")) {
            while (resultSet.next()) {
                LocalDateTime oddt = resultSet.getTimestamp("oddt").toLocalDateTime();
                int idmt = resultSet.getInt("idmt");
                String cone = resultSet.getString("cone");
                String ctwo = resultSet.getString("ctwo");
                String idlg = resultSet.getString("idlg");
                double cfw1 = resultSet.getDouble("cfw1");
                double cfdw = resultSet.getDouble("cfdw");
                double cfw2 = resultSet.getDouble("cfw2");
                double cf1x = resultSet.getDouble("cf1x");
                double cf12 = resultSet.getDouble("cf12");
                double cfx2 = resultSet.getDouble("cfx2");
                double for1 = resultSet.getDouble("for1");
                double cff1 = resultSet.getDouble("cff1");
                double for2 = resultSet.getDouble("for2");
                double cff2 = resultSet.getDouble("cff2");
                double cftmin = resultSet.getDouble("cftmin");
                double tota = resultSet.getDouble("tota");
                double cftmax = resultSet.getDouble("cftmax");
                int tone1 = resultSet.getInt("tone1");
                int tone2 = resultSet.getInt("tone2");
                int ttwo1 = resultSet.getInt("ttwo1");
                int ttwo2 = resultSet.getInt("ttwo2");
                String canc = resultSet.getString("canc");

                GameZenit game = new GameZenit(oddt, cone, ctwo, idmt, idlg, cfw1, cfdw, cfw2, cf1x, cf12,
                        cfx2, for1, cff1, for2, cff2, cftmin, tota, cftmax, tone1, tone2, ttwo1, ttwo2, canc);
                gameZenitInDB.add(game);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gameZenitInDB;
    }

    private void saveGameZenit(ArrayList<GameZenit> gameZenit) {
        ArrayList<GameZenit> gameZenitInDB = getGameZenitInDB();
        ArrayList<GameZenit> resultArr = new ArrayList<>();

        for (GameZenit game: gameZenit) {
            boolean in = false;
            for (GameZenit gameInDB: gameZenitInDB) {
                in = gameInDB.equals(game);
                if (in)
                    break;
            }
            if (!in)
            resultArr.add(game);
        }

        if (resultArr.size() > 0) {
            System.out.println("Добавлено новых строк: " + resultArr.size());
            try (PreparedStatement pstmt = connection.prepareStatement("INSERT INTO games_zenit VALUES (DEFAULT, localtimestamp, to_timestamp(?, ?), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                for (GameZenit game: resultArr) {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter("log.txt", true))) {
                        writer.write("В БД: " + game + '\n');
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    pstmt.setString(1, game.getDateTimeMatch().format(DateTimeFormatter.ofPattern(PATTERN_SAVE_DATE)));
                    pstmt.setString(2, PATTERN_SAVE_DATE_DB);
                    pstmt.setInt(3, game.getId_match());
                    pstmt.setString(4, game.getTeam_one());
                    pstmt.setString(5, game.getTeam_two());
                    pstmt.setString(6, game.getId_league());
                    pstmt.setDouble(7, game.getCoeff_win1());
                    pstmt.setDouble(8, game.getCoeff_draw());
                    pstmt.setDouble(9, game.getCoeff_win2());
                    pstmt.setDouble(10, game.getFora1());
                    pstmt.setDouble(11, game.getCoeff_fora1());
                    pstmt.setDouble(12, game.getFora2());
                    pstmt.setDouble(13, game.getCoeff_fora2());
                    pstmt.setDouble(14, game.getCoeff_total_min());
                    pstmt.setDouble(15, game.getTotal());
                    pstmt.setDouble(16, game.getCoeff_total_max());
                    pstmt.setDouble(17, game.getCoeff_1x());
                    pstmt.setDouble(18, game.getCoeff_12());
                    pstmt.setDouble(19, game.getCoeff_x2());
                    pstmt.setInt(20, game.getScde());
                    pstmt.setInt(21, game.getTone1());
                    pstmt.setInt(22, game.getTone2());
                    pstmt.setInt(23, game.getTtwo1());
                    pstmt.setInt(24, game.getTtwo2());
                    pstmt.setString(25, game.getCanc());

                    pstmt.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
