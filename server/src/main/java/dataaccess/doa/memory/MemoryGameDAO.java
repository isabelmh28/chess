/**
 * MemoryGameDAO Module
 * Game DataObject for non-persistent (in memory) tests
 * NOTE: only use in testing
 * Author: Isabel Hinton
 */

package dataaccess.doa.memory;

import dataaccess.doa.interfaces.GameDAO;
import dataaccess.exceptions.DataAccessException;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Stores games (mapped by gameID)
 * NOTE: DataAccessExceptions thrown for SQL side but must still be implemented
 */
public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();

    // Interface Functions
    @Override
    public void createGame(GameData game) throws DataAccessException {
        if(    (game == null)
            || (game.gameID() < 0)
            || ((game.whiteUsername() != null) && (game.whiteUsername().isEmpty()))
            || ((game.blackUsername() != null) && (game.blackUsername().isEmpty()))
            || (game.gameName() == null) || (game.gameName().isEmpty())
            || (game.game() == null)) {
            throw new DataAccessException("Bad GameData, unable to create game");
        }
        games.put(game.gameID(), game);
    }
    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        if(gameID < 0) {
            throw new DataAccessException("Bad gameID, unable to get game");
        }
        return games.get(gameID);
    }
    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if(    (game == null)
            || (game.gameID() < 0)
            || ((game.whiteUsername() != null) && (game.whiteUsername().isEmpty()))
            || ((game.blackUsername() != null) && (game.blackUsername().isEmpty()))
            || (game.gameName() == null) || (game.gameName().isEmpty())
            || (game.game() == null)) {
            throw new DataAccessException("Bad GameData, unable to create game");
        }
        games.put(game.gameID(), game);
    }
    @Override
    public Collection<GameData> getGameList() throws DataAccessException {
        return new ArrayList<>(games.values());
    }
    @Override
    public void clear() throws DataAccessException {
        games.clear();
    }

    // Override Functions
    @Override
    public int hashCode() {
        return games.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        // Check reference equality
        if(this == obj) {return true;}
        // Check null and see if Classes match
        if((obj == null) || (getClass() != obj.getClass())) {return false;}
        // Cast and Compare data
        MemoryGameDAO that = (MemoryGameDAO) obj;
        return games.equals(that.games);
    }
}
