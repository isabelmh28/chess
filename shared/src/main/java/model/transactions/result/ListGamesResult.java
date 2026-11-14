/**
 * ListGamesResult Module
 * Contains the information returned by a successful
 * listing of active games, which is a collection of GameData sets.
 * Author: Isabel Hinton
 */

package model.transactions.result;

import model.GameData;

import java.util.Collection;

public record ListGamesResult (Collection<GameData> games){ }
