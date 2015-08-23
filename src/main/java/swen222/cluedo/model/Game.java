package swen222.cluedo.model;

import swen222.cluedo.CluedoInterface;
import swen222.cluedo.model.card.Card;
import swen222.cluedo.model.card.Room;
import swen222.cluedo.model.card.Weapon;

import java.util.*;
import java.util.stream.Collectors;

public class Game {
    public final Board board;
    public final Suggestion solution;
    public final Set<Card> cardsNotInPlay;
    public final List<Player> allPlayers;

    public Game(Board board, Suggestion solution, List<Player> players) {
        this.board = board;
        this.solution = solution;
        this.allPlayers = players;

        this.cardsNotInPlay = Card.allCards();
        this.cardsNotInPlay.removeIf(
                card -> players.stream()
                        .map(player -> player.cards)
                        .anyMatch(playerCards -> playerCards.contains(card)));
        this.cardsNotInPlay.remove(this.solution.room);
        this.cardsNotInPlay.remove(this.solution.weapon);
        this.cardsNotInPlay.remove(this.solution.character);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Game)) {
            return false;
        }
        Game other = (Game)obj;

        return super.equals(obj) &&
                this.board.equals(other.board) &&
                this.solution.equals(other.solution) &&
                this.cardsNotInPlay.equals(other.cardsNotInPlay) &&
                this.allPlayers.equals(other.allPlayers);
    }

    /**
     * Returns allPlayers minus 'player', offset so that the player immediately after 'player' is at the start
     * of the returned list.
     */
    private List<Player> otherPlayersFrom(Player player) {
        int index = allPlayers.indexOf(player);
        List<Player> retVal = new ArrayList<>(this.allPlayers.subList(index + 1, allPlayers.size()));
        retVal.addAll(this.allPlayers.subList(0, index));
        return retVal;
    }

    private boolean checkGameOver(List<Player> players) {
        if (players.isEmpty()) {
            for (Player p : this.allPlayers) {
                p.cluedoInterface.notifyGameOver();
            }
            return true;
        }
        return false;
    }

    /**
     * @param playersInPlay The players who are currently playing the game.
     * @param player        the player with the option to make the accusation.
     * @return true if an accusation was made
     */
    private boolean checkForAccusation(List<Player> playersInPlay, Player player) {
        Optional<Suggestion> accusation = player.cluedoInterface.requestPlayerAccusation(player);
        if (accusation.isPresent()) {
            if (accusation.get().equals(this.solution)) {
                CluedoInterface cI = null;
                for (Player p : this.allPlayers) {
                    if (p.cluedoInterface != cI) { //don't notify the same interface object multiple times.
                        p.cluedoInterface.notifySuccess(player);
                        cI = p.cluedoInterface;
                    }
                }
            } else {
                CluedoInterface cI = null;
                for (Player p : this.allPlayers) {
                    if (p.cluedoInterface != cI) { //don't notify the same interface object multiple times.
                        p.cluedoInterface.notifyFailure(player);
                        cI = p.cluedoInterface;
                    }
                }

                int i = playersInPlay.indexOf(player);
                List<Player> newPlayers = new ArrayList<>(playersInPlay.subList(i + 1, playersInPlay.size()));
                newPlayers.addAll(playersInPlay.subList(0, i)); //The play loop now starts from the next player and skips over the player who made the incorrect accusation.
                this.gameLoop(newPlayers);
            }
            return true;
        }
        return false;
    }

    private boolean checkForSuggestion(Player player, Room room, Map<Room, Weapon> weaponLocations) {
        Optional<Suggestion> suggestion = player.cluedoInterface.requestPlayerSuggestion(player, room);
        if (suggestion.isPresent()) {
            weaponLocations.put(room, suggestion.get().weapon);

            SuggestionResponse response = SuggestionResponse.UnableToDisprove();

            for (Player otherPlayer : this.otherPlayersFrom(player)) {
                List<SuggestionResponse> possibleResponses = otherPlayer.possibleResponses(suggestion.get());
                if (possibleResponses.size() > 0) {
                    response = otherPlayer.cluedoInterface.requestPlayerResponse(otherPlayer, possibleResponses);
                    break;
                }
            }

            player.cluedoInterface.notifyPlayerResponse(player, response);
            return true;
        }
        return false;
    }

    /** Returns the remaining number of moves possible. */
    private int performMoveForPlayer(Player player, List<Player> players, Set<Location<Integer>> blockedLocations, int distance) {

        Board.Path moveSequence = player.cluedoInterface.requestPlayerMove(player, this.board, blockedLocations, distance);
        Location<Integer> newLocation = moveSequence.locations[moveSequence.distance - 1];
        int remaining = distance - moveSequence.cost;
        player.setLocation(newLocation);
        return remaining;
    }

    public void gameLoop(List<Player> players) {

        Map<Room, Weapon> weaponLocations = new HashMap<>();

        if (this.checkGameOver(players)) {
            return;
        }

        while (true) {
            for (Player player : players) {

                int diceRoll = (int) (Math.floor(Math.random() * 11) + 2);
                int remaining = diceRoll;

                boolean canMakeSuggestion = true;

                player.cluedoInterface.notifyStartOfTurn(player, diceRoll);

                EnumSet<TurnOption> possibleActions = EnumSet.of(TurnOption.EndTurn, TurnOption.Move, TurnOption.Accusation);

                Set<Location<Integer>> blockedLocations = players.stream()
                        .filter(player1 -> player1 != player &&
                                !this.board.tileAtLocation(player1.location()).room.isPresent())
                        .map(Player::location)
                        .collect(Collectors.toSet());

                player.cluedoInterface.showGame(this, blockedLocations, weaponLocations);

                while (!possibleActions.isEmpty()) {

                    Board.Tile tile = this.board.tileAtLocation(player.location());

                    if (tile.room.isPresent() && canMakeSuggestion) {
                        possibleActions.add(TurnOption.Suggestion);
                    }

                    switch (player.cluedoInterface.requestPlayerChoiceForTurn(possibleActions, player, remaining)) {
                        case Accusation:
                            if (this.checkForAccusation(players, player)) {
                                return;
                            }
                            break;
                        case Suggestion:
                            if (this.checkForSuggestion(player, tile.room.get(), weaponLocations)) {
                                possibleActions.remove(TurnOption.Move);
                                possibleActions.remove(TurnOption.Suggestion);
                                canMakeSuggestion = false;
                            }
                            break;
                        case EndTurn:
                            possibleActions.clear();
                            break;
                        case Move:
                            remaining = this.performMoveForPlayer(player, players, blockedLocations, remaining);
                            if (remaining == 0) {
                                possibleActions.remove(TurnOption.Move);
                            }
                            if (!this.board.tileAtLocation(player.location()).room.isPresent()) {
                                possibleActions.remove(TurnOption.Suggestion);
                            }
                            break;
                    }
                }
            }
        }
    }


}