package swen222.cluedo.game.model;

import swen222.cluedo.game.model.card.Room;
import swen222.cluedo.game.model.card.Weapon;
import swen222.cluedo.game.model.card.Character;


/**
 * A suggestion as to what character, weapon, and room caused the death
 * of Dr. Black.
 *
 * Suggestions are equal to one another if all three of the variables are equal. Simple.
 */
public class Suggestion {

    public final Character character;

    public final Weapon weapon;

    public final Room room;

    public Suggestion(Character character, Weapon weapon, Room room) {
        this.character = character;
        this.weapon = weapon;
        this.room = room;
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
        int result = character != null ? character.hashCode() : 0;
        result = 31 * result + (weapon != null ? weapon.hashCode() : 0);
        result = 31 * result + (room != null ? room.hashCode() : 0);
        return result;
    }
}
