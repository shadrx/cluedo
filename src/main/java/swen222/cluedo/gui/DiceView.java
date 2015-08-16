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

    public DiceView() {
        setDiceValue(7);
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
        g.setColor(Color.red);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.black);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int gap = DiceSize/10;

        int startX = getWidth()/2 - DiceSize; // move back two dice lengths
        int startY = getHeight()/2 - HalfDiceSize;

        drawDice(g2, _split.x, startX, startY, DiceSize);
        drawDice(g2, _split.y, startX + DiceSize + gap, startY, DiceSize);
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
    public Dimension getPreferredSize() {
        return new Dimension(200, 100);
    }

    // this is horrible. there will be a better way.
    private static final Map<Integer, List<Pair<Integer, Integer>>> splitDiceValueMap;
    static {
        Map<Integer, List<Pair<Integer, Integer>>> splitDiceValueMapModifiable = new HashMap<>();

        List<Pair<Integer, Integer>> two = new ArrayList<>();
        two.add(new Pair<>(1, 1));
        List<Pair<Integer, Integer>> three = new ArrayList<>();
        three.add(new Pair<>(1, 2));
        three.add(new Pair<>(2, 1));
        List<Pair<Integer, Integer>> four = new ArrayList<>();
        four.add(new Pair<>(3, 1));
        four.add(new Pair<>(1, 3));
        four.add(new Pair<>(2, 2));
        List<Pair<Integer, Integer>> five = new ArrayList<>();
        five.add(new Pair<>(1, 4));
        five.add(new Pair<>(4, 1));
        five.add(new Pair<>(3, 2));
        five.add(new Pair<>(2, 3));
        List<Pair<Integer, Integer>> six = new ArrayList<>();
        six.add(new Pair<>(1, 5));
        six.add(new Pair<>(5, 1));
        six.add(new Pair<>(4, 2));
        six.add(new Pair<>(2, 4));
        six.add(new Pair<>(3, 3));
        List<Pair<Integer, Integer>> seven = new ArrayList<>();
        seven.add(new Pair<>(1, 6));
        seven.add(new Pair<>(6, 1));
        seven.add(new Pair<>(5, 2));
        seven.add(new Pair<>(2, 5));
        seven.add(new Pair<>(3, 4));
        seven.add(new Pair<>(4, 3));
        List<Pair<Integer, Integer>> eight = new ArrayList<>();
        eight.add(new Pair<>(2, 6));
        eight.add(new Pair<>(6, 2));
        eight.add(new Pair<>(5, 3));
        eight.add(new Pair<>(3, 5));
        eight.add(new Pair<>(4, 4));
        List<Pair<Integer, Integer>> nine = new ArrayList<>();
        nine.add(new Pair<>(3, 6));
        nine.add(new Pair<>(6, 3));
        nine.add(new Pair<>(5, 4));
        nine.add(new Pair<>(4, 5));
        List<Pair<Integer, Integer>> ten = new ArrayList<>();
        ten.add(new Pair<>(4, 6));
        ten.add(new Pair<>(6, 4));
        ten.add(new Pair<>(5, 5));
        List<Pair<Integer, Integer>> eleven = new ArrayList<>();
        eleven.add(new Pair<>(5, 6));
        eleven.add(new Pair<>(6, 5));
        List<Pair<Integer, Integer>> twelve = new ArrayList<>();
        twelve.add(new Pair<>(6, 6));

        splitDiceValueMapModifiable.put(2, two);
        splitDiceValueMapModifiable.put(3, three);
        splitDiceValueMapModifiable.put(4, four);
        splitDiceValueMapModifiable.put(5, five);
        splitDiceValueMapModifiable.put(6, six);
        splitDiceValueMapModifiable.put(7, seven);
        splitDiceValueMapModifiable.put(8, eight);
        splitDiceValueMapModifiable.put(9, nine);
        splitDiceValueMapModifiable.put(10, ten);
        splitDiceValueMapModifiable.put(11, eleven);
        splitDiceValueMapModifiable.put(12, twelve);

        splitDiceValueMap = Collections.unmodifiableMap(splitDiceValueMapModifiable);
    }
}
