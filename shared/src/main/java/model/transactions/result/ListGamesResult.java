package model.transactions.result;

import model.GameData;
import java.util.Collection;
/**
 * ListGamesResult. Contains the information returned by a successful
 * listing of active games, which is a collection of GameData sets.
 */
public record ListGamesResult (Collection<GameData> games){ }
