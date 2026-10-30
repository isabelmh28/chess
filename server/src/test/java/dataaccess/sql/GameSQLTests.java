package dataaccess.sql;

import dataaccess.DatabaseManager;
import dataaccess.doa.sql.SQLGameDAO;
import dataaccess.exceptions.DataAccessException;
import chess.ChessGame;
import model.GameData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Collection;
import java.sql.SQLException;
import com.google.gson.Gson;

import static org.junit.jupiter.api.Assertions.*;

public class GameSQLTests {
    private final SQLGameDAO gameDAO = new SQLGameDAO();
    Gson serializer = new Gson();

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO.clear();
    }

    // Helper to aid in asserts
    private void assertGameInTable(GameData data, ChessGame game) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(
                     "SELECT gameID, whiteUsername, blackUsername, gameName, gameJson " +
                             "FROM game WHERE gameID = ?")
        ) {
            statement.setInt(1, data.gameID());
            var ret = statement.executeQuery();
            String expectedGameJson = serializer.toJson(game);
            assertTrue(ret.next(), "game Row should exist in the database");
            assertEquals(data.gameID(), ret.getInt("gameID"),
                    "Created game should have input gameID");
            assertEquals(data.whiteUsername(), ret.getString("whiteUsername"),
                    "Created game should have input whiteUsername");
            assertEquals(data.blackUsername(), ret.getString("blackUsername"),
                    "Created game should have input blackUsername");
            assertEquals(data.gameName(), ret.getString("gameName"),
                    "Created game should have input gameName");
            assertEquals(expectedGameJson, ret.getString("gameJson"),
                    "Created game should have matching serialized gameJson");
        } catch (SQLException e) {
            throw new DataAccessException(String.format("createGame was unsuccessful: %s", e.getMessage()));
        }
    }

    // Create Game
    @Test
    void createGameAllFieldsFilledSuccess() throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData data = new GameData(12345, "Isa", "Jeff", "firstGame", game);
        gameDAO.createGame(data);
        assertGameInTable(data, game);
    }
    @Test
    void createGameNullWhiteSuccess() throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData data = new GameData(12345, null, "Jeff", "firstGame", game);
        gameDAO.createGame(data);
        assertGameInTable(data, game);
    }
    @Test
    void createGameNullBlackSuccess() throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData data = new GameData(12345, "Isa", null, "firstGame", game);
        gameDAO.createGame(data);
        assertGameInTable(data, game);
    }
    @Test
    void createGameDuplicateDataAccessException() throws DataAccessException {
        ChessGame game1 = new ChessGame();
        ChessGame game2 = new ChessGame();
        GameData data1 = new GameData(12345, "Isa", "Jeff", "firstGame", game1);
        GameData data2 = new GameData(12345, null, null, "SecondGame", game2);
        gameDAO.createGame(data1);

        assertThrows(DataAccessException.class, ()-> gameDAO.createGame(data2),
                "Creating GameData with a duplicate gameID should throw a DataAccessException");
    }

    // Get Game
    @Test
    void getGameSuccess() throws DataAccessException {
        ChessGame game1 = new ChessGame();
        ChessGame game2 = new ChessGame();
        GameData data1 = new GameData(12345, "Isa", "Jeff", "firstGame", game1);
        GameData data2 = new GameData(54321, "Charles", "Aerin", "secondGame", game2);
        gameDAO.createGame(data1);
        gameDAO.createGame(data2);

        GameData get = gameDAO.getGame(data1.gameID());

        assertNotNull(get, "getGameData output should not be null");
        assertEquals(data1.gameID(), get.gameID(),
                "Retrieved gameID should be the same as the original");
        assertEquals(data1.whiteUsername(), get.whiteUsername(),
                "Retrieved whiteUsername should be the same as the original");
        assertEquals(data1.blackUsername(), get.blackUsername(),
                "Retrieved blackUsername should be the same as the original");
        assertEquals(data1.gameName(), get.gameName(),
                "Retrieved gameName should be the same as the original");
        assertEquals(data1.game(), get.game(),
                "Retrieved game should be the same as the original");
    }
    @Test
    void getGameNotFoundNull() throws DataAccessException {
        GameData notFound = gameDAO.getGame(9999999);
        assertNull(notFound, "Searching for GameData not in the database should return Null");
    }

    // Update Game
    @Test
    void updateGameSuccess() throws DataAccessException {
        ChessGame game1 = new ChessGame();
        ChessGame game2 = new ChessGame();
        GameData data1 = new GameData(12345, "Isa", "Jeff", "firstGame", game1);
        GameData data2 = new GameData(12345, "Charles", "Aerin", "secondGame", game2);
        gameDAO.createGame(data1);
        gameDAO.updateGame(data2);
        GameData updated = gameDAO.getGame(data2.gameID());

        assertNotNull(updated, "updateGame output should not be null");
        assertEquals(data1.gameID(), updated.gameID(),
                "Updated gameID should be the same as the original ID");
        assertEquals(data2.whiteUsername(), updated.whiteUsername(),
                "Updated whiteUsername should be the same as the desired update");
        assertEquals(data2.blackUsername(), updated.blackUsername(),
                "Updated blackUsername should be the same as the desired update");
        assertEquals(data2.gameName(), updated.gameName(),
                "Updated gameName should be the same as the desired update");
        assertEquals(data2.game(), updated.game(),
                "Updated game should be the same as the desired update");
    }
    @Test
    void updateGamePartialUpdateSuccess() throws DataAccessException {
        ChessGame game1 = new ChessGame();
        GameData data1 = new GameData(12345, "Isa", "Jeff", "firstGame", game1);
        GameData data2 = new GameData(12345, "Charles", "Jeff", "firstGame", game1);
        gameDAO.createGame(data1);
        gameDAO.updateGame(data2);
        GameData updated = gameDAO.getGame(data2.gameID());

        assertNotNull(updated, "updateGame output should not be null");
        assertEquals(data1.gameID(), updated.gameID(),
                "Updated gameID should be the same as the original ID");
        assertEquals(data2.whiteUsername(), updated.whiteUsername(),
                "Updated whiteUsername should be the same as the desired update");
        assertEquals(data1.blackUsername(), updated.blackUsername(),
                "Updated blackUsername should be the same as the original since it was not updated");
        assertEquals(data1.gameName(), updated.gameName(),
                "Updated gameName should be the same as the original since it was not updated");
        assertEquals(data1.game(), updated.game(),
                "Updated game should be the same as the original since it was not updated");
    }

    // Get Game List
    @Test
    void getGameListSuccess() throws DataAccessException {
        ChessGame game1 = new ChessGame();
        ChessGame game2 = new ChessGame();
        ChessGame game3 = new ChessGame();
        GameData data1 = new GameData(12345, "Isa", "Jeff", "firstGame", game1);
        GameData data2 = new GameData(54321, "Charles", "Aerin", "secondGame", game2);
        GameData data3 = new GameData(13524, null, null, "thirdGame", game3);
        gameDAO.createGame(data1);
        gameDAO.createGame(data2);
        gameDAO.createGame(data3);

        Collection<GameData> gameList = gameDAO.getGameList();

        assertNotNull(gameList, "getGameList output should not be null");
        assertEquals(3, gameList.size(), "There should be 3 games in gameList");

        boolean found1 = false;
        boolean found2 = false;
        boolean found3 = false;
        boolean allFound = true;
        for(GameData game : gameList) {
            if(game.gameID() == data1.gameID()) {
                assertEquals(data1.gameID(), game.gameID(), "Game 1 gameID found");
                assertEquals(data1.whiteUsername(), game.whiteUsername(), "Game 1 whiteUsername found");
                assertEquals(data1.blackUsername(), game.blackUsername(), "Game 1 blackUsername found");
                assertEquals(data1.gameName(), game.gameName(), "Game 1 gameName found");
                assertEquals(data1.game(), game.game(), "Game 1 game found");
                found1 = true;
            } else if(game.gameID() == data2.gameID()) {
                assertEquals(data2.gameID(), game.gameID(), "Game 2 gameID found");
                assertEquals(data2.whiteUsername(), game.whiteUsername(), "Game 2 whiteUsername found");
                assertEquals(data2.blackUsername(), game.blackUsername(), "Game 2 blackUsername found");
                assertEquals(data2.gameName(), game.gameName(), "Game 2 gameName found");
                assertEquals(data2.game(), game.game(), "Game 2 game found");
                found2 = true;
            } else if(game.gameID() == data3.gameID()) {
                assertEquals(data3.gameID(), game.gameID(), "Game 3 gameID found");
                assertEquals(data3.whiteUsername(), game.whiteUsername(), "Game 3 whiteUsername found");
                assertEquals(data3.blackUsername(), game.blackUsername(), "Game 3 blackUsername found");
                assertEquals(data3.gameName(), game.gameName(), "Game 3 gameName found");
                assertEquals(data3.game(), game.game(), "Game 3 game found");
                found3 = true;
            } else {allFound = false;}//listing games that weren't created
        }
        allFound &= found1 & found2 & found3;
        assertTrue(allFound, "All games in the list should be validated and there should be no other games.");
    }
    @Test
    void getGameListEmptySuccess() throws DataAccessException {
        Collection<GameData> emptyList = gameDAO.getGameList();
        assertNotNull(emptyList, "Getting the gameList with no games in the table should not return Null");
        assertTrue(emptyList.isEmpty(), "Getting the gameList with no games in the table should return an empty list");
    }
}
