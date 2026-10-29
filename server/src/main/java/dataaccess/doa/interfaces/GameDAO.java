package dataaccess.doa.interfaces;

import java.util.Collection;

import dataaccess.exceptions.DataAccessException;
import model.GameData;

public interface GameDAO {
    void createGame(GameData game) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;
    Collection<GameData> getGameList() throws DataAccessException;
    void clear() throws DataAccessException;
}