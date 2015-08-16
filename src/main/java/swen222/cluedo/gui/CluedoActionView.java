package swen222.cluedo.gui;


import swen222.cluedo.model.card.Card;
import swen222.cluedo.model.card.CluedoCharacter;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

/**
 * A view that holds buttons that trigger actions within the game. 
 */
public class CluedoActionView extends JComponent {

    public interface CluedoActionListener {
        void didMakeSuggestion();
        void didMakeAccusation();
    }

    private final JButton _makeSuggestion = new JButton("Make Suggestion");
    private final JButton _makeAccusation = new JButton("Make Accusation");

    private Optional<CluedoActionListener> _cluedoActionListener = Optional.empty();

    public CluedoActionView(Optional<CluedoActionListener> cluedoActionListener) {
        this._cluedoActionListener = cluedoActionListener;

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        this.add(_makeAccusation);
        this.add(_makeSuggestion);
    }
}
