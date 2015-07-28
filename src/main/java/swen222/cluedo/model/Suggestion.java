package swen222.cluedo.model;

import com.sun.istack.internal.NotNull;
import swen222.cluedo.model.card.Room;
import swen222.cluedo.model.card.Weapon;
import swen222.cluedo.model.card.CluedoCharacter;


/**
 * A suggestion as to what character, weapon, and room caused the death
 * of Dr. Black.
 *
 * Suggestions are equal to one another if all three of the variables are equal. Simple.
 */
public class Suggestion {

    @NotNull
    public final CluedoCharacter character;

    @NotNull
    public final Weapon weapon;

    @NotNull
    public final Room room;

    public Suggestion(CluedoCharacter character, Weapon weapon, Room room) {
        this.character = character;
        this.weapon = weapon;
        this.room = room;
    }

    public static Suggestion randomSuggestion() {
        CluedoCharacter[] characters = CluedoCharacter.values();
        CluedoCharacter character = characters[(int)(Math.random() * characters.length)];

        Weapon[] weapons = Weapon.values();
        Weapon weapon = weapons[(int)(Math.random() * weapons.length)];

        Room[] rooms = Room.values();
        Room room = rooms[(int)(Math.random() * rooms.length)];

        return new Suggestion(character, weapon, room);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Suggestion that = (Suggestion) o;

        return character == that.character && weapon == that.weapon && room == that.room;
    }

    @Override
    public int hashCode() {
        int result = character.hashCode(); //We don't need to check for null here as it is a bug for any of the fields to be null.
        result = 31 * result + weapon.hashCode();
        result = 31 * result + room.hashCode();
        return result;
    }
}
