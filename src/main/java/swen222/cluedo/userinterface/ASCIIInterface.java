package swen222.cluedo.userinterface;

import swen222.cluedo.model.*;
import swen222.cluedo.model.card.CluedoCharacter;
import swen222.cluedo.model.card.Room;
import swen222.cluedo.model.card.Weapon;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Stream;

public class ASCIIInterface implements CluedoInterface {

    private Scanner scanner;
    private PrintStream out;

    public ASCIIInterface(InputStream in, PrintStream out) {
        this.scanner = new Scanner(in);
        this.out = out;
    }

    /**
     * Prints the list elements to the console, prepended by a number. Then, reads in a number from in and returns the index of the element selected.
     */
    private int selectOptionFromList(Stream<String> list) {
        int i = 1;
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            this.out.printf("%d. %s\n", i, iterator.next());
            i++;
        }

        int option = -1;
        while (option <= 0 || option > i - 1) { //since i == numItems + 1
            option = this.scanner.nextInt();
        }
        return option - 1;
    }

    @Override
    public int getNumberOfPlayers(int min, int max) {
        this.out.printf("How many players (%d - %d)?\n", min, max);

        int numPlayers = 0;
        while (numPlayers < min || numPlayers > max) {
            numPlayers = this.scanner.nextInt();
        }
        return numPlayers;
    }

    @Override
    public CluedoCharacter askToSelectACharacter(List<CluedoCharacter> availableCharacters) {
        Stream<String> characterNames = availableCharacters.stream().map(CluedoCharacter::toString);

        this.out.println("Select a character:\n");

        int index = this.selectOptionFromList(characterNames);
        return availableCharacters.get(index);
    }

    @Override
    public void notifySuccess(Player player) {
        this.out.printf("%s has made a correct accusation and won!", player.character);
    }

    @Override
    public void notifyFailure(Player player) {
        this.out.printf("%s has made an incorrect accusation and is no longer in the game.", player.character);
    }

    @Override
    public void notifyGameOver() {
        this.out.println("There are no players left! The game is now over.");
    }

    @Override
    public void showGameState(GameState gameState) {
        //TODO
    }

    @Override
    public List<Direction> requestPlayerMove(Player player, int distance) {
        this.out.printf("Enter a move of length %d.\n", distance);
        this.out.printf("Moves are in the format UDLR, where such a move would mean go up, then down, then left, then right.\n\n");

        List<Direction> moveSequence = new ArrayList<>();

        String charSequence = this.scanner.next();

        if (charSequence.length() != distance) {
            return this.requestPlayerMove(player, distance);
        }

        for (int i = 0; i < charSequence.length(); i++) {
            char c = charSequence.charAt(i);
            Direction dir = Direction.fromCharacter(c);
            if (dir == null) {
                return this.requestPlayerMove(player, distance);
            }
            moveSequence.add(dir);
        }

        return moveSequence;
    }

    @Override
    public Optional<Suggestion> requestPlayerAccusation(Player player) {
        char userWantsToMakeAccusation = '\0';

        while (userWantsToMakeAccusation != 'Y' && userWantsToMakeAccusation != 'N') {
            this.out.println("Do you want to make an accusation (Y/N)?");
            userWantsToMakeAccusation = this.scanner.next().charAt(0);
        }

        if (userWantsToMakeAccusation == 'Y') {
            this.out.println("Choose your suspect:");

            Stream<String> suspects = Arrays.stream(CluedoCharacter.values()).map(CluedoCharacter::toString);
            CluedoCharacter suspect = CluedoCharacter.values()[this.selectOptionFromList(suspects)];

            this.out.println("Choose your weapon:");

            Stream<String> weapons = Arrays.stream(Weapon.values()).map(Weapon::toString);
            Weapon weapon = Weapon.values()[this.selectOptionFromList(weapons)];

            this.out.println("Choose your room:");

            Stream<String> rooms = Arrays.stream(Room.values()).map(Room::toString);
            Room room = Room.values()[this.selectOptionFromList(rooms)];

            return Optional.of(new Suggestion(suspect, weapon, room));

        } else {
            return Optional.empty();
        }

    }

    @Override
    public Optional<Suggestion> requestPlayerSuggestion(Player player, Room room) {
        char userWantsToMakeSuggestion = '\0';

        while (userWantsToMakeSuggestion != 'Y' && userWantsToMakeSuggestion != 'N') {
            this.out.println("Do you want to make an suggestion (Y/N)?");
            userWantsToMakeSuggestion = this.scanner.next().charAt(0);
        }

        if (userWantsToMakeSuggestion == 'Y') {
            this.out.printf("You suggest it was done in the %s, by: \n", room);

            Stream<String> suspects = Arrays.stream(CluedoCharacter.values()).map(CluedoCharacter::toString);
            CluedoCharacter suspect = CluedoCharacter.values()[this.selectOptionFromList(suspects)];

            this.out.println("with the ");

            Stream<String> weapons = Arrays.stream(Weapon.values()).map(Weapon::toString);
            Weapon weapon = Weapon.values()[this.selectOptionFromList(weapons)];

            return Optional.of(new Suggestion(suspect, weapon, room));

        } else {
            return Optional.empty();
        }
    }

    @Override
    public SuggestionResponse requestPlayerResponse(Player player, List<SuggestionResponse> possibleResponses) {
        this.out.printf("%s can disprove this suggestion. %s, what do you want to disprove?\n", player.character, player.character);

        Stream<String> responseStrings = possibleResponses.stream().map(SuggestionResponse::toString);
        return possibleResponses.get(this.selectOptionFromList(responseStrings));
    }

    @Override
    public void notifyPlayerResponse(Player player, SuggestionResponse response) {
        this.out.printf("%s: %s", player.character, response);
    }
}
