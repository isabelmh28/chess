package transactions.result;

import model.GameData;
import java.util.Collection;

record ListGamesResult (Collection<GameData> games){ }
