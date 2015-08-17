package swen222.cluedo.gui;

import swen222.cluedo.model.Player;
import swen222.cluedo.model.Suggestion;
import swen222.cluedo.model.card.CluedoCharacter;
import swen222.cluedo.model.card.Room;
import swen222.cluedo.model.card.Weapon;
import utilities.SpringUtilities;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.List;

public class PlayerSuggestionDialog extends JDialog {

    public interface PlayerSuggestionDelegate {
        void playerDidMakeSuggestion(PlayerSuggestionDialog dialog, Suggestion suggestion);
        void playerDidCancelSuggestion(PlayerSuggestionDialog dialog);
    }

    private final Optional<PlayerSuggestionDelegate> _delegate;

    private CluedoCharacter _character;
    private Weapon _weapon;
    private Room _room;


    /**
     * Creates a new PlayerSuggestionDialog, which is used for making either a suggestion or an accusation.
     * @param delegate The (optional) delegate, to be notified when the player has made their suggestion.
     * @param room If the room is present, it is considered a suggestion; otherwise, it's an accusation.
     */
    public PlayerSuggestionDialog(JFrame parent, PlayerSuggestionDelegate delegate, Player player, Optional<Room> room) {
        super(parent, room.isPresent() ? "Make a Suggestion" : "Make an Accusation");
        _delegate = Optional.ofNullable(delegate);

        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        if (room.isPresent()) {
            _room = room.get();
        }

        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());

        this.add(this.setupDataEntryPanel(player, room), BorderLayout.CENTER);


        this.add(this.setupButtonsPanel(), BorderLayout.SOUTH);

        //Display the window.
        this.pack();
        this.setVisible(true);
    }

    private JPanel setupDataEntryPanel(Player player, Optional<Room> room) {
        JPanel dataEntryPanel = new JPanel(new SpringLayout());

        JLabel characterLabel = new JLabel(room.isPresent() ? "You suggest it was done by" : "You accuse", JLabel.TRAILING);
        dataEntryPanel.add(characterLabel);

        CluedoCharacter[] characters = CluedoCharacter.values();

        if (!room.isPresent()) { //it's an accusation, so remove the player's cards.
            List<CluedoCharacter> characterList = new ArrayList<>(Arrays.asList(characters));
            characterList.removeIf((player.cards::contains));
            characters = characterList.toArray(new CluedoCharacter[characterList.size()]);
        }
        _character = characters[0];

        JComboBox<CluedoCharacter> characterComboBox = new JComboBox<>(characters);
        characterComboBox.addActionListener((action) -> _character = (CluedoCharacter) characterComboBox.getSelectedItem());
        dataEntryPanel.add(characterComboBox);
        characterLabel.setLabelFor(characterComboBox);

        JLabel weaponLabel = new JLabel(room.isPresent() ? "with the" : "of using the", JLabel.TRAILING);
        dataEntryPanel.add(weaponLabel);
        Weapon[] weapons = Weapon.values();

        if (!room.isPresent()) { //it's an accusation, so remove the player's cards.
            List<Weapon> weaponList = new ArrayList<>(Arrays.asList(weapons));
            weaponList.removeIf((player.cards::contains));
            weapons = weaponList.toArray(new Weapon[weaponList.size()]);
        }

        _weapon = weapons[0];

        JComboBox<Weapon> weaponComboBox = new JComboBox<>(weapons);
        weaponComboBox.addActionListener((action) -> _weapon = (Weapon) weaponComboBox.getSelectedItem());
        dataEntryPanel.add(weaponComboBox);
        weaponLabel.setLabelFor(weaponComboBox);

        JLabel roomLabel = new JLabel("in the", JLabel.TRAILING);
        dataEntryPanel.add(roomLabel);

        Room[] rooms = room.isPresent() ? new Room[]{room.get()} : Room.values();

        if (!room.isPresent()) { //it's an accusation, so remove the player's cards.
            List<Room> roomList = new ArrayList<>(Arrays.asList(rooms));
            roomList.removeIf((player.cards::contains));
            rooms = roomList.toArray(new Room[roomList.size()]);
        }

        _room = rooms[0];

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

        return dataEntryPanel;
    }

    private JPanel setupButtonsPanel() {
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

        return buttonsPanel;
    }
}
