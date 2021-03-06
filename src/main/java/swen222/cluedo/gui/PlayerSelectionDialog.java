package swen222.cluedo.gui;

import swen222.cluedo.model.Player;
import swen222.cluedo.model.card.CluedoCharacter;
import utilities.SpringUtilities;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Optional;
import java.util.Set;

/**
 * PlayerSelectionDialog is a self-displaying dialog used for determining a player's name and character.
 * It has an optional delegate to be notified of the result.
 * It can only be dismissed when a valid name (e.g. of non-zero length) has been entered and a character has been selected.
 */
public class PlayerSelectionDialog extends JDialog {

    private final Optional<PlayerSelectionDelegate> _delegate;

    //Adapted from http://docs.oracle.com/javase/tutorial/uiswing/examples/layout/SpringDemo4Project/src/layout/SpringDemo4.java
    private String _playerName;
    private CluedoCharacter _selectedCharacter;
    private Player _player;
    public PlayerSelectionDialog(PlayerSelectionDelegate delegate, Set<CluedoCharacter> availableCharacters) {
        super((JFrame)null, "Create a New Player");
        _delegate = Optional.ofNullable(delegate);

        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());


        JPanel dataEntryPanel = new JPanel(new SpringLayout());

        JLabel nameLabel = new JLabel("Name:", JLabel.TRAILING);
        dataEntryPanel.add(nameLabel);
        JTextField nameField = new JTextField(10);
        nameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                _playerName = nameField.getText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                _playerName = nameField.getText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                _playerName = nameField.getText();
            }
        });
        nameLabel.setLabelFor(nameField);
        dataEntryPanel.add(nameField);

        dataEntryPanel.add(new JLabel("Choose a character:", JLabel.TRAILING));

        CluedoCharacter[] allCharacters = CluedoCharacter.values();

        ButtonGroup buttonGroup = new ButtonGroup();

        for (int i = 0; i < allCharacters.length; i++) {
            CluedoCharacter character = allCharacters[i];
            JRadioButton button = new JRadioButton(character.toString());

            button.addActionListener((action) -> _selectedCharacter = character);

            button.setEnabled(availableCharacters.contains(character));
            buttonGroup.add(button);

            dataEntryPanel.add(button);

            if (i + 1 != allCharacters.length) {
                dataEntryPanel.add(Box.createHorizontalBox());
            }
        }

        //Lay out the panel.
        SpringUtilities.makeCompactGrid(dataEntryPanel,
                allCharacters.length + 1, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad

        this.add(dataEntryPanel, BorderLayout.CENTER);

        JButton closeButton = new JButton("OK");
        closeButton.addActionListener((action) -> {
            if (_selectedCharacter != null && _playerName != null && _playerName.length() > 0) {
                if (_delegate.isPresent()) {
                    _delegate.get().playerSelectionDialogDidCreatePlayer(PlayerSelectionDialog.this, _playerName, _selectedCharacter);
                }
                this.dispose();
            }
        });

        this.add(closeButton, BorderLayout.SOUTH);

        //Display the window.
        this.pack();
        this.setLocationRelativeTo(null); //center on the screen.
        this.setVisible(true);
    }

    public interface PlayerSelectionDelegate {
        void playerSelectionDialogDidCreatePlayer(PlayerSelectionDialog dialog, String name, CluedoCharacter character);
    }
}
