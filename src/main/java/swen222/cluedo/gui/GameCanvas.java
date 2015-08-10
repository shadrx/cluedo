package swen222.cluedo.gui;

import swen222.cluedo.model.Game;

import javax.swing.*;
import java.awt.*;

public class GameCanvas extends JPanel {

    private Game _gameState; //TODO
    private Game _previousGameState = null; //TODO

    public void setGameState(Game gameState) {
        _previousGameState = _gameState;
        _gameState = gameState;
    }

    @Override
    protected void paintComponent(Graphics g) {
        float ratio = _gameState.board.width / _gameState.board.height;

        int width = this.getWidth();
        int height = this.getHeight();

        width = Math.min(width, (int)(height * ratio));
        height = Math.min(height, (int)(width / ratio));

        int startX = this.getWidth()/2 - width/2;
        int startY = this.getHeight()/2 - height/2;

        g.setColor(Color.yellow);
        g.fillRect(startX, startY, width, height);

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 600);
    }
}
