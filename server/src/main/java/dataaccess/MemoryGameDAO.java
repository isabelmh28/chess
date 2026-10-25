package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Stores games (mapped by gameID)
 * NOTE: DataAccessExceptions thrown for SQL side but must still be implemented
 */
public class MemoryGameDAO implements GameDAO{
    private final HashMap<Integer, GameData> games = new HashMap<>();

    // Interface Functions
    @Override
    public void createGame(GameData game) throws DataAccessException {
        games.put(game.gameID(), game);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
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
