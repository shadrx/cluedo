package swen222.cluedo.gui;

import swen222.cluedo.model.Game;
import swen222.cluedo.model.card.Card;
import swen222.cluedo.model.card.CluedoCharacter;
import swen222.cluedo.model.card.Room;
import swen222.cluedo.model.card.Weapon;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Optional;

public class CluedoFrame extends JFrame {

    public GameCanvas canvas;

    /**
     * A panel to contain components at the bottom of the frame.
     */
    private JPanel bottomPanel;

    private CluedoActionView actionView;
    private CardView cardView;
    private DiceView diceView;

    public CluedoFrame() {
        super("Cluedo");
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        // setup the game canvas that draw the board
        this.setLayout(new BorderLayout());
        this.canvas = new GameCanvas();
        this.add(canvas, BorderLayout.CENTER);

        this.actionView = new CluedoActionView(Optional.empty());

        // add card view
        this.cardView = new CardView(Arrays.asList(new Card[]{CluedoCharacter.MrsWhite, Weapon.Rope, Room.Ballroom, CluedoCharacter.ColonelMustard, Weapon.Candlestick}), Optional.empty());
        this.diceView = new DiceView();

        // setup the bottom panel
        this.bottomPanel = new JPanel();
        this.bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));
        this.bottomPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        this.bottomPanel.add(diceView, BorderLayout.WEST);
        this.bottomPanel.add(Box.createHorizontalStrut(10));
        this.bottomPanel.add(cardView, BorderLayout.CENTER);
        this.bottomPanel.add(Box.createHorizontalStrut(10));
        this.bottomPanel.add(actionView, BorderLayout.EAST);

        this.add(bottomPanel, BorderLayout.SOUTH);

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.pack();
        this.setResizable(true);
        this.setVisible(true);
    }
}
