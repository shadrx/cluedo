package swen222.cluedo.gui;

import swen222.cluedo.model.*;
import swen222.cluedo.model.card.CluedoCharacter;
import swen222.cluedo.model.card.Room;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

public class GameCanvas extends JPanel {

    public interface TileSelectionDelegate {
        void didSelectTileAtLocation(Location<Integer> location);
    }

    private static final int WallWidth = 4;
    private static final double PlayerDiameterRatio = 0.6f; //0.7 * the size of the tile.
    private static final Font GameFont = new Font(null, Font.BOLD, 14);

    private static final double TilesPerMillisecond = 6.f/1000.f;

    private Game _gameState = null;

    private Optional<TileSelectionDelegate> _tileSelectionDelegate;

    private java.util.List<Location<Integer>> _lastPlayerMove = new ArrayList<>();
    private CluedoCharacter _lastPlayerMoveCharacter = null;
    private double _moveSequencePosition = -1.f;

    private Set<Location<Integer>[]> _accessibleTilePaths = null;

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


        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                double width = GameCanvas.this.getWidth();
                double height = GameCanvas.this.getHeight();
                Board board = _gameState.board;
                double ratio = (double)board.width / board.height;
                width = Math.min(width, (height * ratio));
                height = Math.min(height, (width / ratio));
                double startX = GameCanvas.this.getWidth()/2 - width/2;
                double startY = GameCanvas.this.getHeight()/2 - height/2;
                double step = width / board.width;

                int tileX = (int)((e.getX() - startX)/step);
                int tileY = (int)((e.getY() - startY)/step);
                if (_tileSelectionDelegate.isPresent() && tileX >= 0 && tileX < board.width && tileY >= 0 && tileY < board.height) {
                    _tileSelectionDelegate.get().didSelectTileAtLocation(new Location<>(tileX, tileY));
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }

    public void setDelegate(TileSelectionDelegate delegate) {
        _tileSelectionDelegate = Optional.ofNullable(delegate);
    }

    public void setGameState(Game gameState) {
        _gameState = gameState;
        this.repaint();
    }

    public void setAccessibleTilePaths(Set<Location<Integer>[]> paths) {
        _accessibleTilePaths = paths;
        this.repaint();
    }

    private boolean shouldPlayMoveSequence() {
        return _gameState != null && _lastPlayerMove != null && _moveSequencePosition != _lastPlayerMove.size() - 1;
    }

    public void setLastPlayerMove(List<Direction> move, Player player) {
        _lastPlayerMove.clear();

        Location<Integer> previousLocation = player.location();
        _lastPlayerMove.add(previousLocation);

        for (Direction direction : move) {
            Location<Integer> nextLocation = previousLocation.locationInDirection(direction); //Convert the list of directions to a list of locations.
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
        double tileCentreX = tileSize/2.0;
        double tileCentreY = tileSize/2.0;

        if (board.hasWallBetween(location, location.locationInDirection(Direction.Up))) {
            tileCentreY += WallWidth/4.0;
        }

        if (board.hasWallBetween(location, location.locationInDirection(Direction.Down))) {
            tileCentreY -= WallWidth/4.0;
        }

        if (board.hasWallBetween(location, location.locationInDirection(Direction.Left))) {
            tileCentreX += WallWidth/4.0;
        }
        if (board.hasWallBetween(location, location.locationInDirection(Direction.Right))) {
            tileCentreX -= WallWidth/4.0;
        }


        return new Location<>((float)(startX + tileSize * location.x + tileCentreX),(float)(startY + tileSize * location.y + tileCentreY));
    }

    private void drawPlayer(Graphics g, Location<Float> location, CluedoCharacter character, double step) {

        //TODO these are not correctly centered and sometimes lack borders.

        double diameter = step * PlayerDiameterRatio;

        final double characterBorderRatio = 1.2f;

        g.setColor(Color.black);
        g.fillOval(round(location.x - diameter * characterBorderRatio / 2), round(location.y - diameter * characterBorderRatio / 2), round(diameter * characterBorderRatio), round(diameter * characterBorderRatio));

        g.setColor(character.colour());
        g.fillOval(round(location.x - diameter/2), round(location.y - diameter/2), round(diameter), round(diameter));
    }

    private void drawAccessibleTilesOverlay(Graphics g, Set<Location<Integer>[]> paths, double startX, double startY, double tileSize) {
        for (Location<Integer>[] path : paths) {
            Location<Integer> endTile = path[path.length - 1];

            g.setColor(new Color(0.8f, 0.2f, 0.3f, 1.f - path.length/14.f));

            g.fillRect(round(startX + tileSize * endTile.x), round(startY + tileSize * endTile.y), round(tileSize), round(tileSize));
        }
    }

    @SuppressWarnings("SuspiciousNameCombination") //so we don't get warned about using 'WallWidth' in conjunction with height
    @Override
    protected void paintComponent(Graphics g) {

        double width = this.getWidth();
        double height = this.getHeight();

        Graphics2D graphics2D = (Graphics2D) g;

        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
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
                    g.fillRect(round(x * step + startX - 0.5), round(y * step + startY - 0.5), round(step + 1), round(step + 1));
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
                    g.fillRect(round(startX + step * (x + 1) - WallWidth/2), round(startY + step * y - 0.5), WallWidth, round(step + 1));
                }

                if (board.hasWallBetween(location, new Location<>(x, y + 1))) {
                    g.fillRect(round(startX + step * x - 0.5), round(startY + step * (y + 1) - WallWidth/2), round(step + 1), WallWidth);
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

        boolean shouldPlayMoveSequence = this.shouldPlayMoveSequence();
        for (Player player : _gameState.allPlayers) {
            Location<Float> playerLocation;
            if (shouldPlayMoveSequence && //We haven't finished animating the move
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

            this.drawPlayer(g, playerLocation, player.character, step);

        }

        if (_accessibleTilePaths != null && !shouldPlayMoveSequence) {
            this.drawAccessibleTilesOverlay(g, _accessibleTilePaths, startX, startY, step);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 600);
    }
}
