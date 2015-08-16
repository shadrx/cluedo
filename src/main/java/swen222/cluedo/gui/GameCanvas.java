package swen222.cluedo.gui;

import swen222.cluedo.model.*;
import swen222.cluedo.model.card.CluedoCharacter;
import swen222.cluedo.model.card.Room;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class GameCanvas extends JPanel {

    private static final int WallWidth = 4;
    private static final double PlayerDiameterRatio = 0.7f; //0.7 * the size of the tile.
    private static final Font GameFont = new Font("Verdana", Font.BOLD, 14);

    private static final double TilesPerMillisecond = 6.f/1000.f;

    private Game _previousGameState = null;
    private Game _gameState = null;

    private java.util.List<Location<Integer>> _lastPlayerMove = new ArrayList<>();
    private CluedoCharacter _lastPlayerMoveCharacter = null;
    private double _moveSequencePosition = -1.f;

    public GameCanvas() {
        super(true);

        Timer runLoopTimer = new Timer(1000/60, (action) -> {
            if (this.shouldPlayMoveSequence()) {
                this.update(1000/60);
                this.repaint();
            }
        });
        runLoopTimer.setRepeats(true);
        runLoopTimer.start();
    }

    public void setGameState(Game gameState) {
        _previousGameState = _gameState;
        _gameState = gameState;
        this.repaint();
    }

    private boolean shouldPlayMoveSequence() {
        return _gameState != null && _lastPlayerMove != null && _moveSequencePosition != _lastPlayerMove.size() - 1 && !_gameState.equals(_previousGameState);
    }

    public void setLastPlayerMove(List<Direction> move, Player player) {
        _lastPlayerMove.clear();

        Location<Integer> previousLocation = player.location();
        _lastPlayerMove.add(previousLocation);

        for (Direction direction : move) {
            Location<Integer> nextLocation = previousLocation.locationInDirection(direction);
            _lastPlayerMove.add(nextLocation);
            previousLocation = nextLocation;
        }

        _lastPlayerMoveCharacter = player.character;
        _moveSequencePosition = 0.f;
    }

    private void update(int deltaTime) {
        double advanceMoveBy = deltaTime * TilesPerMillisecond;
        _moveSequencePosition = Math.min(advanceMoveBy + _moveSequencePosition, _lastPlayerMove.size() - 1.f);
    }

    private int round(double f) {
        return (int)Math.round(f);
    }

    /**
     * Returns the location, in pixels, of the centre of a tile at a given location, taking the walls into account.
     */
    private Location<Float> centreForTileAtLocation(Location<Integer> location, Board board, double startX, double startY, double tileSize) {
        double tileCentreX = tileSize/2.f;
        double tileCentreY = tileSize/2.f;

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


        return new Location<>((float)(startX + tileSize * location.x + tileCentreX),(float)(startY + tileSize * location.y + tileCentreY));
    }

    private void drawPlayer(Graphics g, Location<Float> location, CluedoCharacter character, double startX, double startY, double step) {
        double diameter = step * PlayerDiameterRatio;

        final double characterBorderRatio = 1.2f;
        double characterEdgeInset = (step - diameter * characterBorderRatio)/4.f;

        g.setColor(Color.black);
        g.fillOval(round(location.x - diameter * characterBorderRatio / 2 + characterEdgeInset), round(location.y - diameter * characterBorderRatio / 2 + characterEdgeInset), round(diameter * characterBorderRatio), round(diameter * characterBorderRatio));

        g.setColor(character.colour());
        g.fillOval(round(location.x - diameter/2 + characterEdgeInset), round(location.y - diameter/2 + characterEdgeInset), round(diameter), round(diameter));
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

        double width = this.getWidth();
        double height = this.getHeight();

        Graphics2D graphics2D = (Graphics2D) g;

        //Set  anti-alias!
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Set anti-alias for text
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Board board = _gameState.board;

        double ratio = (double)board.width / board.height;

        width = Math.min(width, (height * ratio));
        height = Math.min(height, (width / ratio));

        double startX = this.getWidth()/2 - width/2;
        double startY = this.getHeight()/2 - height/2;

        g.setColor(Color.yellow);
        g.fillRect(round(startX), round(startY), round(width), round(height));

        //Draw grid

        g.setColor(Color.black);

        double step = width / board.width;

        for (double x = startX + step; x < startX + width; x += step) {
            g.drawLine(round(x), round(startY), round(x), round(startY + height));
        }
        for (double y = startY + step; y < startY + height; y += step) {
            g.drawLine(round(startX), round(y), round(startX + width), round(y));
        }


        int x = 0;
        for (Board.Tile[] column : board.tiles) {
            int y = 0;
            for (Board.Tile tile : column) {
                boolean hasRoom = tile.room.isPresent();
                boolean isUnaccessibleSpace = tile.adjacentLocations.isEmpty();
                if (hasRoom || isUnaccessibleSpace) {
                    g.setColor(hasRoom ? Color.lightGray : Color.cyan);
                    g.fillRect(round(x * step + startX), round(y * step + startY), round(step), round(step));
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
                    g.fillRect(round(startX + step * (x + 1) - 2), round(startY + step * y), WallWidth, round(step));
                }

                if (board.hasWallBetween(location, new Location<>(x, y + 1))) {
                    g.fillRect(round(startX + step * x), round(startY + step * (y + 1) - 2), round(step), WallWidth);
                }

                y++;
            }

            x++;
        }

        g.fillRect(round(startX), round(startY), WallWidth, round(height));
        g.fillRect(round(startX), round(startY), round(width), WallWidth);
        g.fillRect(round(startX + width - WallWidth), round(startY), WallWidth, round(height));
        g.fillRect(round(startX), round(startY + height - WallWidth), round(width), WallWidth);


        g.setFont(GameFont);
        g.setColor(Color.black);
        FontMetrics fontMetrics = g.getFontMetrics(GameFont);

        for (Room room : Room.values()) {
            Location<Float> centre = board.centreLocationForRoom(room);
            double centreX = (centre.x + 0.5f) * step + startX;
            double centreY = (centre.y + 0.5f) * step + startY;

            String name = room.shortName().toUpperCase();

            Rectangle2D bounds = fontMetrics.getStringBounds(name, g);

            g.drawString(name, (int)(centreX - bounds.getCenterX()), (int)(centreY - bounds.getCenterY()));
        }

        for (Player player : _gameState.allPlayers) {
            Location<Float> playerLocation = null;
            if (this.shouldPlayMoveSequence() && //We haven't finished animating the move
                    player.character == _lastPlayerMoveCharacter) { //The move is for this character

                int lowIndex = (int)Math.floor(_moveSequencePosition);
                int highIndex = (int)Math.ceil(_moveSequencePosition);
                double lerpValue = _moveSequencePosition - lowIndex;

                Location<Float> startLocation = this.centreForTileAtLocation(_lastPlayerMove.get(lowIndex), board, startX, startY, step);
                Location<Float> endLocation = this.centreForTileAtLocation(_lastPlayerMove.get(highIndex), board, startX, startY, step);
                playerLocation = Location.lerp(startLocation, endLocation, (float)lerpValue);

            } else {
                playerLocation = this.centreForTileAtLocation(player.location(), board, startX, startY, step);
            }

            this.drawPlayer(g, playerLocation, player.character, startX, startY, step);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 600);
    }
}
