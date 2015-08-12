package swen222.cluedo.gui;

import swen222.cluedo.model.*;
import swen222.cluedo.model.card.Room;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Set;

public class GameCanvas extends JPanel {

    private static final int WallWidth = 4;
    private static final float PlayerDiameterRatio = 0.7f; //0.7 * the size of the tile.
    private static final Font GameFont = new Font("Verdana", Font.BOLD, 14);

    private Game _gameState; //TODO
    private Game _previousGameState = null; //TODO

    public GameCanvas() {
        super(true);
    }

    public void setGameState(Game gameState) {
        _previousGameState = _gameState;
        _gameState = gameState;
    }

    /**
     * Returns the location, in pixels, of the centre of a tile at a given location, taking the walls into account.
     */
    private Location<Float> centreForTileAtLocation(Location<Integer> location, Board board, int startX, int startY, int tileSize) {
        float tileCentreX = tileSize/2.f;
        float tileCentreY = tileSize/2.f;

        if (board.hasWallBetween(location, location.locationInDirection(Direction.Up))) {
            tileCentreY += WallWidth/2.f;
        }

        if (board.hasWallBetween(location, location.locationInDirection(Direction.Down))) {
            tileCentreY -= WallWidth/2.f;
        }

        if (board.hasWallBetween(location, location.locationInDirection(Direction.Left))) {
            tileCentreX += WallWidth/2.f;
        }
        if (board.hasWallBetween(location, location.locationInDirection(Direction.Right))) {
            tileCentreX -= WallWidth/2.f;
        }


        return new Location<>(startX + tileSize * location.x + tileCentreX, startY + tileSize * location.y + tileCentreY);
    }

    private void drawAccessibleTilesOverlay(Graphics g, Set<Location<Integer>[]> paths, int startX, int startY, int tileSize) {
        for (Location<Integer>[] path : paths) {
            Location<Integer> endTile = path[path.length - 1];

            g.setColor(new Color(0.8f, 0.2f, 0.3f, 1.f - path.length/11.f));

            g.fillRect(startX + tileSize * endTile.x, startY + tileSize * endTile.y, tileSize, tileSize);
        }
    }

    @SuppressWarnings("SuspiciousNameCombination") //so we don't get warned about using 'WallWidth' in conjunction with height
    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D graphics2D = (Graphics2D) g;

        //Set  anti-alias!
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Set anti-alias for text
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

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

        final int step = width / board.width;

        for (int x = startX + step; x < width; x += step) {
            g.drawLine(x, startY, x, startY + height);
        }
        for (int y = startY + step; y < height; y += step) {
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
                    g.fillRect(x * step + startX, y * step + startY, step, step);
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
                    g.fillRect(startX + step * (x + 1) - 2, startY + step * y, WallWidth, step);
                }

                if (board.hasWallBetween(location, new Location<>(x, y + 1))) {
                    g.fillRect(startX + step * x, startY + step * (y + 1) - 2, step, WallWidth);
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
            float centreX = (centre.x + 0.5f) * step + startX;
            float centreY = (centre.y + 0.5f) * step + startY;

            String name = room.shortName().toUpperCase();

            Rectangle2D bounds = fontMetrics.getStringBounds(name, g);

            g.drawString(name, (int)(centreX - bounds.getCenterX()), (int)(centreY - bounds.getCenterY()));
        }

        for (Player player : _gameState.allPlayers) {

            Location<Float> playerLocation = this.centreForTileAtLocation(player.location(), board, startX, startY, step);

            float diameter = step * PlayerDiameterRatio;

            final float characterBorderRatio = 1.2f;
            float characterEdgeInset = (step - diameter * characterBorderRatio)/4.f;

            g.setColor(Color.black);
            g.fillOval((int) (playerLocation.x - diameter * characterBorderRatio / 2 + characterEdgeInset), (int) (playerLocation.y - diameter * characterBorderRatio / 2 + characterEdgeInset), (int) (diameter * characterBorderRatio), (int)(diameter * characterBorderRatio));

            g.setColor(player.character.colour());
            g.fillOval((int)(playerLocation.x - diameter/2 + characterEdgeInset), (int)(playerLocation.y - diameter/2 + characterEdgeInset), (int)diameter, (int)diameter);
        }

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 600);
    }
}
