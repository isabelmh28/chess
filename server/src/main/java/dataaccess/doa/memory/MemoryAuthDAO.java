package dataaccess.doa.memory;

import dataaccess.doa.interfaces.AuthDAO;
import dataaccess.exceptions.DataAccessException;
import model.AuthData;
import java.util.HashMap;

/**
 * Stores auths (mapped by authToken)
 * NOTE: DataAccessExceptions thrown for SQL side but must still be implemented
 */
public class MemoryAuthDAO implements AuthDAO {
    private final HashMap<String, AuthData> auths = new HashMap<>();

    // Interface Functions
    @Override
    public void createAuthData(AuthData auth) throws DataAccessException {
        auths.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException {
        return auths.get(authToken);
    }

    @Override
    public void removeAuthData(String authToken) throws DataAccessException {
        auths.remove(authToken);
    }

    @Override
    public void clear() throws DataAccessException {
        auths.clear();
    }

    // Override Functions
    @Override
    public int hashCode() {
        return auths.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        // Check reference equality
        if(this == obj) {return true;}
        // Check null and see if Classes match
        if((obj == null) || (getClass() != obj.getClass())) {return false;}
        // Cast and Compare data
        MemoryAuthDAO that = (MemoryAuthDAO) obj;
        return auths.equals(that.auths);
    }
}
