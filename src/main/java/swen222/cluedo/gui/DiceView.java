package swen222.cluedo.gui;

import utilities.Pair;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.List;

/**
 * A view that displays the result of two six sided dice.
 */
public class DiceView extends JPanel {

    private static final int DiceSize = 50;
    private static final int HalfDiceSize = DiceSize/2;

    private Pair<Integer, Integer> _split;

    private int _remainingValue;

    public DiceView(int diceValue) {
        this.setDiceValue(diceValue);
    }

    /**
     * Sets the total value to display between the two dice.
     *
     * @param value the value to display between the two dice
     */
    public void setDiceValue(int value) {
        // generate a random split of the values for each dice face. there is a better way.
        List<Pair<Integer, Integer>> possibleFaceValues = splitDiceValueMap.get(value);
        Collections.shuffle(possibleFaceValues);
        _split = possibleFaceValues.get(0);

        repaint();
    }

    /**
     * Sets the remaining amount of value that can be used.
     *
     * @param remainingValue the remaining amount of value that can be used
     */
    public void setRemainingValue(int remainingValue) {
        this._remainingValue = remainingValue;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int diceSize = Math.min(DiceSize, (int)(this.getWidth()/2.1));
        int halfDiceSize = diceSize/2;

        int gap = diceSize/10;

        int startX = getWidth()/2 - diceSize; // move back two dice lengths
        int startY = getHeight()/2 - halfDiceSize;

        drawDice(g2, _split.x, startX, startY, diceSize);
        drawDice(g2, _split.y, startX + diceSize + gap, startY, diceSize);
    }


    /**
     * Draws a dice on the component
     *
     * @param g2 graphics 2d object
     * @param value the value to show on the dice face
     * @param x the x position to start drawing the dice from
     * @param y the y position to start drawing the dice from
     * @param size the size of the dice
     */
    private void drawDice(Graphics2D g2, int value, int x, int y, int size) {
        if (value <= 0 || value > 6) {
            throw new IllegalArgumentException("Face value for dice must be between 1-6");
        }

        RoundRectangle2D diceBorder = new RoundRectangle2D.Float(x, y, size, size, 10, 10);
        g2.draw(diceBorder);
        g2.setColor(Color.white);
        g2.fill(diceBorder);

        g2.setColor(Color.black);

        // positioning variables
        int dotSize = size/6;
        int padding = size/6;

        int halfway = size/2;
        int yCentre = y + halfway - dotSize/2;
        int xCentre = x + halfway - dotSize/2;
        int xLeft = x + padding;
        int xRight = x + size - padding - dotSize;
        int yTop = y + padding;
        int yBottom = y + size - padding - dotSize;

        // draw dot in centre for odd numbered values
        if (value % 2 != 0) {
            g2.fillOval(xCentre, yCentre, dotSize, dotSize);
        }

        // top right and bottom left dots
        if (value > 1) {
            g2.fillOval(xRight, yTop, dotSize, dotSize);
            g2.fillOval(xLeft, yBottom, dotSize, dotSize);
        }

        // top left and bottom right dots
        if (value > 3) {
            g2.fillOval(xLeft, yTop, dotSize, dotSize);
            g2.fillOval(xRight, yBottom, dotSize, dotSize);
        }

        // centre left and centre right dots
        if (value == 6) {
            g2.fillOval(xLeft, yCentre, dotSize, dotSize);
            g2.fillOval(xRight, yCentre, dotSize, dotSize);
        }
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(80, 100);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 100);
    }


    private static final Map<Integer, List<Pair<Integer, Integer>>> splitDiceValueMap;
    static {
        Map<Integer, List<Pair<Integer, Integer>>> splitDiceValueMapModifiable = new HashMap<>();

        for (int i = 1; i <= 6; i++) {
            for (int j = 1; j <= 6; j++) {
                int sum = i + j;
                List<Pair<Integer, Integer>> numbersForSum = splitDiceValueMapModifiable.get(sum);
                if (numbersForSum == null) {
                    numbersForSum = new ArrayList<>();
                    splitDiceValueMapModifiable.put(sum, numbersForSum);
                }

                numbersForSum.add(new Pair<>(i, j));
            }
        }
        splitDiceValueMap = Collections.unmodifiableMap(splitDiceValueMapModifiable);
    }
}
