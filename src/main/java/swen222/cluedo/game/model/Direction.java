package swen222.cluedo.game.model;

public enum Direction {
    Up, Down, Left, Right;

    public static Direction fromCharacter(char c) {
        switch (c) {
            case 'U':
                return Direction.Up;
            case 'D':
                return Direction.Down;
            case 'L':
                return Direction.Left;
            case 'R':
                return Direction.Right;
            default:
                return null;
        }
    }
}
