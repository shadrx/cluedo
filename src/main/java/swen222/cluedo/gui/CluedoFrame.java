package swen222.cluedo.gui;

import swen222.cluedo.model.card.Card;
import swen222.cluedo.model.card.CluedoCharacter;
import swen222.cluedo.model.card.Room;
import swen222.cluedo.model.card.Weapon;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

public class CluedoFrame extends JFrame {


    /**
     * A panel to contain components at the bottom of the frame.
     */
    private JPanel _bottomPanel;

    private CluedoActionView _actionView;
    private CardView _cardView;
    private DiceView _diceView;
    private GameCanvas _canvas;

    public CluedoFrame() {
        super("Cluedo");
        this.createAndShowGUI();
    }

    private void createAndShowGUI() {
        // setup the game canvas that draw the board
        this.setLayout(new BorderLayout());
        _canvas = new GameCanvas();
        this.add(_canvas, BorderLayout.CENTER);

        _actionView = new CluedoActionView(Optional.empty());

        // add card view
        _cardView = new CardView(Collections.emptyList(), Optional.empty());
        _diceView = new DiceView(7);

        // setup the bottom panel
        this._bottomPanel = new JPanel();
        this._bottomPanel.setLayout(new BoxLayout(_bottomPanel, BoxLayout.LINE_AXIS));
        this._bottomPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        this._bottomPanel.add(_diceView, BorderLayout.WEST);
        this._bottomPanel.add(Box.createHorizontalStrut(10));

        JScrollPane cardScrollPane = new JScrollPane(_cardView, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        cardScrollPane.setOpaque(false);
        Dimension cardViewMaxSize = _cardView.getMaximumSize();
        cardScrollPane.setMaximumSize(new Dimension(cardViewMaxSize.width, Integer.MAX_VALUE));

        this._bottomPanel.add(cardScrollPane, BorderLayout.CENTER);
        this._bottomPanel.add(Box.createHorizontalStrut(10));
        this._bottomPanel.add(_actionView, BorderLayout.EAST);

        this.add(_bottomPanel, BorderLayout.SOUTH);

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.pack();
        this.setResizable(true);
        this.setVisible(true);
    }

    public CardView cardView() {
        return _cardView;
    }

    public DiceView diceView() {
        return _diceView;
    }

    public CluedoActionView actionView() {
        return _actionView;
    }

    public GameCanvas canvas() {
        return _canvas;
    }
}
