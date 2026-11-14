package model;

import chess.ChessGame;
/**
 * GameData record. Contains a game's unique id, name, and game information.
 * Also contains the players, storing their usernames.
 */
public record GameData(
        int       gameID,
        String    whiteUsername,
        String    blackUsername,
        String    gameName,
        ChessGame game
) { }
