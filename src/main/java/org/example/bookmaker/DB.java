package org.example.bookmaker;

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
    private static final String SQL_FOR_READY_MATCH = "select count(*) as c from (select extract(epoch FROM age(oddt, LOCALTIMESTAMP))/60 tim from games_zenit where oddt > LOCALTIMESTAMP) tab where tab.tim between " + ((Zenit.getSleepMin() / 60) - 1) + " and " + ((Zenit.getSleepMin() / 60) + 3);
    private static final String SQL_FOR_READY_RESULT = "select count(*) as c from games_zenit where scde!=1 and oddt<localtimestamp and idmt not in (select idmt from result)";

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
        saveTeamZenit(zenit.getMapTeams());
        saveLeagueZenit(zenit.getMapLeague());
        saveGameZenit(zenit.getGameZenitArrayList());
    }

    public void saveResult(Map<Integer, Result> mapResult) {
        ArrayList<Integer> resultInDB = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery("SELECT * FROM Result")) {
            while (resultSet.next()) {
                resultInDB.add(resultSet.getInt("idmt"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Iterator<Map.Entry<Integer, Result>> itr = mapResult.entrySet().iterator();
        while(itr.hasNext()) {
            Map.Entry<Integer, Result> entry =  itr.next();
            for (Integer key: resultInDB) {
                if (key.equals(entry.getKey())) {
                    itr.remove();
                    break;
                }
            }
        }

        if (mapResult.size()>0) {
            try (PreparedStatement pstmt = connection.prepareStatement("INSERT INTO Result VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)")) {
                for (Integer key: mapResult.keySet()) {
                    pstmt.setInt(1, key);
                    pstmt.setInt(2, mapResult.get(key).getTone1());
                    pstmt.setInt(3, mapResult.get(key).getTone2());
                    pstmt.setInt(4, mapResult.get(key).getTtwo1());
                    pstmt.setInt(5, mapResult.get(key).getTtwo2());
                    if (mapResult.get(key).getCanc() == null) {
                        pstmt.setNull(6, Types.VARCHAR);
                    } else {
                        pstmt.setString(6, mapResult.get(key).getCanc());
                    }
                    pstmt.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        try (PreparedStatement pstmt = connection.prepareStatement("UPDATE games_zenit SET scde=1 WHERE oddt<localtimestamp and scde!=1 and idmt in (select idmt from result)")) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getReadyResult() {
        int result = 0;
        try (Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery(SQL_FOR_READY_RESULT)) {
            while (resultSet.next()) {
                result = resultSet.getInt("c");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void saveTeamZenit(Map<Integer, String> mapCmd) {
        Map <Integer, String> mapTeamInDB = new HashMap<>();

        try (Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery("SELECT * FROM Teams")) {
            while (resultSet.next()) {
                mapTeamInDB.put(resultSet.getInt("id_book"), resultSet.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (mapTeamInDB.size()>0 && !mapCmd.equals(mapTeamInDB)) {
            for (Integer key: mapTeamInDB.keySet()) {
                mapCmd.remove(key, mapTeamInDB.get(key));
            }

            if (mapCmd.size() > 0) {
                try (PreparedStatement pstmt = connection.prepareStatement("INSERT INTO Teams VALUES (DEFAULT, ?, ?)")) {
                    for (Integer key: mapCmd.keySet()) {
                        pstmt.setInt(1, key);
                        pstmt.setString(2, mapCmd.get(key));
                        pstmt.executeUpdate();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveLeagueZenit(Map<Integer, String> mapLeague) {
        Map <Integer, String> mapLeagueInDB = new HashMap<>();

        try (Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery("SELECT * FROM League")) {
            while (resultSet.next()) {
                mapLeagueInDB.put(resultSet.getInt("id_book"), resultSet.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (mapLeagueInDB.size()>0 && !mapLeague.equals(mapLeagueInDB)) {
            for (Integer key: mapLeagueInDB.keySet()) {
                mapLeague.remove(key, mapLeagueInDB.get(key));
            }

            if (mapLeague.size() > 0) {
                try (PreparedStatement pstmt = connection.prepareStatement("INSERT INTO League VALUES (DEFAULT, ?, ?)")) {
                    for (Integer key: mapLeague.keySet()) {
                        pstmt.setInt(1, key);
                        pstmt.setString(2, mapLeague.get(key));
                        pstmt.executeUpdate();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
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

    private void saveGameZenit(ArrayList<GameZenit> gameZenit) {
        ArrayList<GameZenit> gameZenitInDB = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery("SELECT * FROM games_zenit")) {
            while (resultSet.next()) {
                LocalDateTime oddt = resultSet.getTimestamp("oddt").toLocalDateTime();
                int idmt = resultSet.getInt("idmt");
                int cone = resultSet.getInt("cone");
                int ctwo = resultSet.getInt("ctwo");
                int idlg = resultSet.getInt("idlg");
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

                GameZenit game = new GameZenit(oddt, idmt, cone, ctwo, idlg, cfw1, cfdw, cfw2, cf1x, cf12, cfx2, for1, cff1, for2, cff2, cftmin, tota, cftmax);
                gameZenitInDB.add(game);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ArrayList<GameZenit> resultArr = new ArrayList<>();

        for (GameZenit gameIn: gameZenit) {
            boolean in = false;
            for (GameZenit gameInDB: gameZenitInDB) {
                in = gameInDB.equals(gameIn);
                if (in)
                    break;
            }
            if (!in)
                resultArr.add(gameIn);
        }

        if (resultArr.size() > 0) {
            try (PreparedStatement pstmt = connection.prepareStatement("INSERT INTO games_zenit VALUES (DEFAULT, localtimestamp, to_timestamp(?, ?), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                for (GameZenit game: resultArr) {
                    pstmt.setString(1, game.getDateTimeMatch().format(DateTimeFormatter.ofPattern(PATTERN_SAVE_DATE)));
                    pstmt.setString(2, PATTERN_SAVE_DATE_DB);
                    pstmt.setInt(3, game.getId_match());
                    pstmt.setInt(4, game.getTeam_one());
                    pstmt.setInt(5, game.getTeam_two());
                    pstmt.setInt(6, game.getId_league());
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
                    pstmt.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}
