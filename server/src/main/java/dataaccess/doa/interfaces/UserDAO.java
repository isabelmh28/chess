package dataaccess.doa.interfaces;

import dataaccess.exceptions.DataAccessException;
import model.UserData;

public interface UserDAO {
    void createUser(UserData data) throws DataAccessException;
    UserData getUserData(String userName) throws DataAccessException;
    void clear() throws DataAccessException;
}
