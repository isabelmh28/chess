/**
 * GameDAO Interface
 * Game DataObject Interface
 * Author: Isabel Hinton
 */

package dataaccess.doa.interfaces;

import dataaccess.exceptions.DataAccessException;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    // Interface Functions
    void createGame(GameData game) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;
    Collection<GameData> getGameList() throws DataAccessException;
    void clear() throws DataAccessException;
}