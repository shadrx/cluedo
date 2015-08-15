package swen222.cluedo.gui;

import swen222.cluedo.model.Player;
import swen222.cluedo.model.Suggestion;
import swen222.cluedo.model.card.CluedoCharacter;
import swen222.cluedo.model.card.Room;
import swen222.cluedo.model.card.Weapon;
import utilities.SpringUtilities;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.html.Option;
import java.awt.*;
import java.util.Optional;
import java.util.Set;

public class PlayerSuggestionDialog extends JDialog {

    public interface PlayerSuggestionDelegate {
        void playerDidMakeSuggestion(PlayerSuggestionDialog dialog, Suggestion suggestion);
        void playerDidCancelSuggestion(PlayerSuggestionDialog dialog);
    }

    private final Optional<PlayerSuggestionDelegate> _delegate;
    private boolean _roomIsLocked = false;

    private CluedoCharacter _character;
    private Weapon _weapon;
    private Room _room;


    /**
     * Creates a new PlayerSuggestionDialog, which is used for making either a suggestion or an accusation.
     * @param delegate The (optional) delegate, to be notified when the player has made their suggestion.
     * @param room If the room is present, it is considered a suggestion; otherwise, it's an accusation.
     */
    public PlayerSuggestionDialog(JFrame parent, PlayerSuggestionDelegate delegate, Optional<Room> room) {
        super(parent, room.isPresent() ? "Make a Suggestion" : "Make an Accusation");
        _delegate = Optional.ofNullable(delegate);

        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        if (room.isPresent()) {
            _room = room.get();
        }

        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());

        JPanel dataEntryPanel = new JPanel(new SpringLayout());

        JLabel characterLabel = new JLabel(room.isPresent() ? "You suggest it was done by" : "You accuse", JLabel.TRAILING);
        dataEntryPanel.add(characterLabel);
        CluedoCharacter[] allCharacters = CluedoCharacter.values();
        JComboBox<CluedoCharacter> characterComboBox = new JComboBox<>(allCharacters);
        characterComboBox.addActionListener((action) -> _character = (CluedoCharacter)characterComboBox.getSelectedItem());
        dataEntryPanel.add(characterComboBox);
        characterLabel.setLabelFor(characterComboBox);


        JLabel weaponLabel = new JLabel(room.isPresent() ? "with the" : "of using the", JLabel.TRAILING);
        dataEntryPanel.add(weaponLabel);
        Weapon[] allWeapons = Weapon.values();
        JComboBox<Weapon> weaponComboBox = new JComboBox<>(allWeapons);
        weaponComboBox.addActionListener((action) -> _weapon = (Weapon)weaponComboBox.getSelectedItem());
        dataEntryPanel.add(weaponComboBox);
        weaponLabel.setLabelFor(weaponComboBox);

        JLabel roomLabel = new JLabel("in the", JLabel.TRAILING);
        dataEntryPanel.add(roomLabel);
        Room[] rooms = room.isPresent() ? new Room[]{room.get()} : Room.values();
        JComboBox<Room> roomComboBox = new JComboBox<>(rooms);
        roomComboBox.setEnabled(!room.isPresent());
        roomComboBox.addActionListener((action) -> _room = (Room) roomComboBox.getSelectedItem());
        dataEntryPanel.add(roomComboBox);
        roomLabel.setLabelFor(roomComboBox);

        //Lay out the panel.
        SpringUtilities.makeCompactGrid(dataEntryPanel,
                3, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad

        this.add(dataEntryPanel, BorderLayout.CENTER);


        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));

        buttonsPanel.add(Box.createHorizontalGlue());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener((action) -> {
            if (_delegate.isPresent()) {
                _delegate.get().playerDidCancelSuggestion(this);
            }
            this.dispose();
        });
        buttonsPanel.add(cancelButton);

        JButton closeButton = new JButton("OK");
        closeButton.addActionListener((action) -> {
            if (_character != null && _weapon != null && _room != null) {
                if (_delegate.isPresent()) {
                    _delegate.get().playerDidMakeSuggestion(this, new Suggestion(_character, _weapon, _room));
                }
                this.dispose();
            }
        });
        buttonsPanel.add(closeButton);

        this.add(buttonsPanel, BorderLayout.SOUTH);

        //Display the window.
        this.pack();
        this.setVisible(true);
    }
}
