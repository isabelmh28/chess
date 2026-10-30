package dataaccess.doa.sql;

import chess.ChessGame;
import model.GameData;
import dataaccess.DatabaseManager;
import dataaccess.doa.interfaces.GameDAO;
import dataaccess.exceptions.DataAccessException;

import com.google.gson.Gson;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Stores games (Primary key is gameID)
 */
public class SQLGameDAO implements GameDAO {
    private final Gson serializer = new Gson();
    // Interface Functions
    @Override
    public void createGame(GameData game) throws DataAccessException {
        var gameJson = serializer.toJson(game.game());
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(
                     "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, gameJson)" +
                             "VALUES (?, ?, ?, ?, ?)")) {
            statement.setInt(1, game.gameID());
            statement.setString(2, game.whiteUsername());
            statement.setString(3, game.blackUsername());
            statement.setString(4, game.gameName());
            statement.setString(5, gameJson);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("Unable to update database in Game: createGame. %s", e.getMessage()));
        }
    }
    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(
                     "SELECT gameID, whiteUsername, blackUsername, gameName, gameJson" +
                             " FROM game WHERE gameID = ?")) {
            statement.setInt(1, gameID);
            try (var ret = statement.executeQuery()) {
                if(ret.next()) {
                    ChessGame game = serializer.fromJson(ret.getString("gameJson"), ChessGame.class);
                    return new GameData(
                            ret.getInt("gameID"),
                            ret.getString("whiteUsername"),
                            ret.getString("blackUsername"),
                            ret.getString("gameName"),
                            game
                    );
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("Unable to retrieve GameData in Game: getGame. %s", e.getMessage()));
        }
        return null;
    }
    @Override
    public void updateGame(GameData game) throws DataAccessException {
        var gameJson = serializer.toJson(game.game());
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(
                "UPDATE game SET whiteUsername = ?, blackUsername = ?, gameName = ?, gameJson = ?" +
                     "WHERE gameID = ?")) {
            statement.setString(1, game.whiteUsername());
            statement.setString(2, game.blackUsername());
            statement.setString(3, game.gameName());
            statement.setString(4, gameJson);
            statement.setInt(5, game.gameID());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("Unable to update GameData in Game: updateGame. %s", e.getMessage()));
        }
    }
    @Override
    public Collection<GameData> getGameList() throws DataAccessException {
        Collection<GameData> gameList = new ArrayList<>();
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(
                     "SELECT gameID, whiteUsername, blackUsername, gameName, gameJson FROM game")) {
            try (var ret = statement.executeQuery()) {
                while(ret.next()) {
                    ChessGame game = serializer.fromJson(ret.getString("gameJson"), ChessGame.class);
                    gameList.add( new GameData(
                            ret.getInt("gameID"),
                            ret.getString("whiteUsername"),
                            ret.getString("blackUsername"),
                            ret.getString("gameName"),
                            game)
                    );
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("Unable to retrieve gameList in Game: getGameList. %s", e.getMessage()));
        }
        return gameList;
    }
    @Override
    public void clear() throws DataAccessException {
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(
                     "DELETE FROM game")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("Unable to clear GameData in Game: clear. %s", e.getMessage()));
        }
    }
}
