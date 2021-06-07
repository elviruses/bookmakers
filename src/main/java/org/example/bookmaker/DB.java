package org.example.bookmaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class DB {

    private static final Logger logger = LoggerFactory.getLogger(DB.class);

    @Value("${db.url}")
    private String URL;
    @Value("${db.username}")
    private String USERNAME;
    @Value("${db.password}")
    private String PASSWORD;
    @Value("${db.pattern_save_date}")
    private String PATTERN_SAVE_DATE;
    @Value("${db.pattern_save_date_db}")
    private String PATTERN_SAVE_DATE_DB;
    @Value("${db.sql_get_ready_match}")
    private String SQL_GET_READY_MATCH;
    @Value("${db.sql_get_game_zenit_in_db}")
    private String SQL_GET_GAME_ZENIT_IN_DB;
    @Value("${db.sql_insert_into_zenit}")
    private String SQL_INSERT_INTO_ZENIT;
    @Value("${db.driver-class-name}")
    private String DRIVER_CLASS_NAME;

    private Connection connection;

    public DB() {
    }

    @PostConstruct
    private void postConstruct() {
        try {
            Class.forName(DRIVER_CLASS_NAME);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void saveZenit(Zenit zenit) {
        saveGameZenit(zenit.getGameZenitList());
    }

    public ArrayList<GameZenit> getGameZenitInDB() {
        ArrayList<GameZenit> gameZenitInDB = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery(SQL_GET_GAME_ZENIT_IN_DB)) {
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
            logger.error(e.getMessage(), e);
        }
        return gameZenitInDB;
    }

    private void saveGameZenit(List<GameZenit> gameZenit) {
        if (gameZenit.size() > 0) {
            logger.info("Добавится новых строк: {}", gameZenit.size());
            try (PreparedStatement pstmt = connection.prepareStatement(SQL_INSERT_INTO_ZENIT)) {
                for (GameZenit game: gameZenit) {
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

//                    logger.info("Добавлена строка в БД: {}", game);
                    logger.info("SQL: {}", pstmt);

                    pstmt.executeUpdate();
                }
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            }
            try (PreparedStatement pstmt = connection.prepareStatement("delete from game_zenit where id=0")) {
                logger.info("Delete row");
                pstmt.executeUpdate();
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            }
            Parser.setZenit_run(false);
        }
    }
}
