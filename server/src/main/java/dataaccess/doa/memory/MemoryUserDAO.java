package dataaccess.doa.memory;

import dataaccess.exceptions.DataAccessException;
import dataaccess.doa.interfaces.UserDAO;
import model.UserData;
import java.util.HashMap;

/**
 * Stores users (mapped by username) in Memory
 * NOTE: DataAccessExceptions thrown for SQL side but must still be implemented
 */
public class MemoryUserDAO implements UserDAO {
    private final HashMap<String, UserData> users = new HashMap<>();

    // Interface Functions
    @Override
    public void createUser(UserData data) throws DataAccessException {
        users.put(data.username(), data);
    }

    @Override
    public UserData getUserData(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void clear() throws DataAccessException {
        users.clear();
    }

    // Override Functions
    @Override
    public int hashCode() {
        return users.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        // Check reference equality
        if(this == obj) {return true;}
        // Check null and see if Classes match
        if((obj == null) || (getClass() != obj.getClass())) {return false;}
        // Cast and Compare data
        MemoryUserDAO that = (MemoryUserDAO) obj;
        return users.equals(that.users);
    }
}
