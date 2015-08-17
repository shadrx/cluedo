package swen222.cluedo.gui;


import javax.swing.*;
import java.util.Optional;

/**
 * A view that holds buttons that trigger actions within the game. 
 */
public class CluedoActionView extends JComponent {

    public interface ActionDelegate {
        void makeAnAccusation();
        void makeASuggestion();
        void endTurn();
    }

    public final JButton suggestionButton = new JButton("Make a Suggestion");
    public final JButton accusationButton = new JButton("Make an Accusation");
    public final JButton endTurnButton = new JButton("End Turn");

    private Optional<ActionDelegate> _delegate = Optional.empty();

    public CluedoActionView(Optional<ActionDelegate> delegate) {
        _delegate = delegate;

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        this.suggestionButton.setAlignmentX(CENTER_ALIGNMENT);
        this.accusationButton.setAlignmentX(CENTER_ALIGNMENT);
        this.endTurnButton.setAlignmentX(CENTER_ALIGNMENT);

        this.accusationButton.addActionListener((action) -> {
            if (_delegate.isPresent()) {
                _delegate.get().makeAnAccusation();
            }
        });

        this.suggestionButton.addActionListener((action) -> {
            if (_delegate.isPresent()) {
                _delegate.get().makeASuggestion();
            }
        });

        this.endTurnButton.addActionListener((action) -> {
            if (_delegate.isPresent()) {
                _delegate.get().endTurn();
            }
        });

        this.add(this.accusationButton);
        this.add(this.suggestionButton);
        this.add(endTurnButton);
    }

    public void setDelegate(ActionDelegate delegate) {
        _delegate = Optional.ofNullable(delegate);
    }
}
