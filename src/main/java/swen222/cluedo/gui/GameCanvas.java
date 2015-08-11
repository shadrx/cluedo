package swen222.cluedo.gui;

import swen222.cluedo.model.Board;
import swen222.cluedo.model.Game;
import swen222.cluedo.model.Location;
import swen222.cluedo.model.Player;
import swen222.cluedo.model.card.Room;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class GameCanvas extends JPanel {

    private static final int WallWidth = 4;
    private static final Font GameFont = new Font("Verdana", Font.BOLD, 14);

    private Game _gameState; //TODO
    private Game _previousGameState = null; //TODO

    public void setGameState(Game gameState) {
        _previousGameState = _gameState;
        _gameState = gameState;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected void paintComponent(Graphics g) {

        Board board = _gameState.board;

        float ratio = (float)board.width / board.height;

        int width = this.getWidth();
        int height = this.getHeight();

        width = Math.min(width, (int)(height * ratio));
        height = Math.min(height, (int)(width / ratio));

        int startX = this.getWidth()/2 - width/2;
        int startY = this.getHeight()/2 - height/2;

        g.setColor(Color.yellow);
        g.fillRect(startX, startY, width, height);

        //Draw grid

        g.setColor(Color.black);

        final int xStep = width / board.width;
        final int yStep = height / board.height;

        for (int x = startX + xStep; x < width; x += xStep) {
            g.drawLine(x, startY, x, startY + height);
        }
        for (int y = startY + yStep; y < height; y += yStep) {
            g.drawLine(startX, y, startX + width, y);
        }


        int x = 0;
        for (Board.Tile[] column : board.tiles) {
            int y = 0;
            for (Board.Tile tile : column) {
                boolean hasRoom = tile.room.isPresent();
                boolean isUnaccessibleSpace = tile.adjacentLocations.isEmpty();
                if (hasRoom || isUnaccessibleSpace) {
                    g.setColor(hasRoom ? Color.lightGray : Color.cyan);
                    g.fillRect(x * xStep + startX, y * yStep + startY, xStep, yStep);
                }

                y++;
            }

            x++;
        }

        //Loop again to draw over what we already have.

        g.setColor(Color.black);
        x = 0;
        for (Board.Tile[] column : board.tiles) {
            int y = 0;
            for (Board.Tile tile : column) {
                Location<Integer> location = new Location<>(x, y);


                if (board.hasWallBetween(location, new Location<>(x + 1, y))) {
                    g.fillRect(startX + xStep * (x + 1) - 2, startY + yStep * y, WallWidth, yStep);
                }

                if (board.hasWallBetween(location, new Location<>(x, y + 1))) {
                    g.fillRect(startX + xStep * x, startY + yStep * (y + 1) - 2, xStep, WallWidth);
                }


                y++;
            }

            x++;
        }

        g.fillRect(startX, startY, WallWidth, height);
        g.fillRect(startX, startY, width, WallWidth);
        g.fillRect(startX + width - WallWidth, startY, WallWidth, height);
        g.fillRect(startX, startY + height - WallWidth, width, WallWidth);


        g.setFont(GameFont);
        g.setColor(Color.black);
        FontMetrics fontMetrics = g.getFontMetrics(GameFont);

        for (Room room : Room.values()) {
            Location<Float> centre = board.centreLocationForRoom(room);
            float centreX = (centre.x + 0.5f) * xStep + startX;
            float centreY = (centre.y + 0.5f) * yStep + startY;

            String name = room.shortName().toUpperCase();

            Rectangle2D bounds = fontMetrics.getStringBounds(name, g);

            g.drawString(name, (int)(centreX - bounds.getCenterX()), (int)(centreY - bounds.getCenterY()));
        }

        for (Player player : _gameState.allPlayers) {
            g.setColor(player.character.colour());
            g.fillOval(player.location().x * xStep + startX + WallWidth, player.location().y * yStep + startY + WallWidth, xStep - 2 * WallWidth, yStep - 2 * WallWidth);
        }

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 600);
    }
}
