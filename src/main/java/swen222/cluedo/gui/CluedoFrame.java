package swen222.cluedo.gui;

import swen222.cluedo.model.TurnOption;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * CluedoFrame is the root view in the MVC paradigm. It provides access to its child components and
 * convenience methods to modify their action delegates and enabled actions.
 * When instantiated, it will automatically display itself.
 */
public class CluedoFrame extends JFrame {

    /**
     * A panel to contain components at the bottom of the frame.
     */
    private JPanel _bottomPanel;

    private CluedoMenuBar _cluedoMenuBar;

    private CluedoActionView _actionView;
    private CardView _cardView;
    private DiceView _diceView;
    private GameCanvas _canvas;

    public CluedoFrame() {
        super("Cluedo");
        this.createAndShowGUI();
    }

    /**
     * Sets the action delegate for the child action view and menu bar.
     *
     * @param delegate The delegate to set. Null is a valid value.
     */
    public void setActionDelegate(ActionDelegate delegate) {
        _actionView.setDelegate(delegate);
        _cluedoMenuBar.setDelegate(delegate);
    }

    /**
     * Restricts the actions that can be performed by the action view and menu bar to those specified in actions.
     *
     * @param actions The set of all possible actions at this time.
     */
    public void setEnabledActions(Set<TurnOption> actions) {
        _actionView.setEnabledActions(actions);
        _cluedoMenuBar.setEnabledActions(actions);
    }

    private void createAndShowGUI() {
        // setup the game canvas that draw the board
        this.setLayout(new BorderLayout());

        _cluedoMenuBar = new CluedoMenuBar();
        this.add(_cluedoMenuBar, BorderLayout.NORTH);

        _canvas = new GameCanvas();
        this.add(_canvas, BorderLayout.CENTER);

        _actionView = new CluedoActionView(Optional.empty());

        // add card view
        _cardView = new CardView(Collections.emptyList(), Optional.empty());
        _diceView = new DiceView(0);

        // setup the bottom panel
        this._bottomPanel = new JPanel();
        this._bottomPanel.setLayout(new BoxLayout(_bottomPanel, BoxLayout.LINE_AXIS));
        this._bottomPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        this._bottomPanel.add(_diceView);
        this._bottomPanel.add(Box.createHorizontalStrut(10));

        JScrollPane cardScrollPane = new JScrollPane(_cardView, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        cardScrollPane.setOpaque(false);
        Dimension cardViewMaxSize = _cardView.getMaximumSize();
        cardScrollPane.setMaximumSize(new Dimension(cardViewMaxSize.width, Integer.MAX_VALUE));

        this._bottomPanel.add(cardScrollPane, BorderLayout.CENTER);
        this._bottomPanel.add(Box.createHorizontalStrut(10));
        this._bottomPanel.add(_actionView, BorderLayout.EAST);

        this.add(_bottomPanel, BorderLayout.SOUTH);

        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int reply = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to quit?", "Quit", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        this.pack();
        this.setResizable(true);
        this.setVisible(true);
    }

    //Public getters.

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
