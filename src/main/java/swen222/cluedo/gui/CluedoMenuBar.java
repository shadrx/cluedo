package swen222.cluedo.gui;

import swen222.cluedo.model.TurnOption;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class CluedoMenuBar extends JMenuBar {

    private Optional<ActionDelegate> _delegate;
    private JMenu _gameMenu;
    private Map<JMenuItem, TurnOption> _menuItemsToTurnOptions = new HashMap<>();

    public CluedoMenuBar() {
        this(null);
    }

    public CluedoMenuBar(ActionDelegate delegate) {
        this.setDelegate(delegate);
        setupFileMenu();
    }

    public void setDelegate(ActionDelegate delegate) {
        _delegate = Optional.ofNullable(delegate);
    }

    private void setupFileMenu() {
        _gameMenu = new JMenu("Game");
        this.add(_gameMenu);

        int inputMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        addMenuItemToMenu(_gameMenu, "Make an Accusation", Optional.of(TurnOption.Accusation), e -> {
            if (_delegate.isPresent()) {
                _delegate.get().makeAnAccusation();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_A, inputMask));

        addMenuItemToMenu(_gameMenu, "Make a Suggestion", Optional.of(TurnOption.Suggestion), e -> {
            if (_delegate.isPresent()) {
                _delegate.get().makeASuggestion();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_S, inputMask));

        addMenuItemToMenu(_gameMenu, "End Turn", Optional.of(TurnOption.EndTurn), e -> {
            if (_delegate.isPresent()) {
                _delegate.get().endTurn();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_E, inputMask));

        addMenuItemToMenu(_gameMenu, "Quit", Optional.empty(), e -> System.exit(0), KeyStroke.getKeyStroke(KeyEvent.VK_Q, inputMask));
    }

    /**
     * Adds a menu item to the cluedo menu bar
     *
     * @param menu the menu to add the item to
     * @param name the name of the menu item
     * @param al the action to be performed when the menu item is used
     * @param keyStroke a key stroke to be used as a shortcut
     */
    private void addMenuItemToMenu(JMenu menu, String name, Optional<TurnOption> turnOption, ActionListener al, KeyStroke keyStroke) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.setAccelerator(keyStroke);
        menuItem.addActionListener(al);
        menu.add(menuItem);

        if (turnOption.isPresent()) {
            _menuItemsToTurnOptions.put(menuItem, turnOption.get());
        }
    }

    public void setEnabledActions(Set<TurnOption> actions) {
        for (int i = 0; i < _gameMenu.getItemCount(); i++) {
            JMenuItem item = _gameMenu.getItem(i);
            TurnOption associatedOption = _menuItemsToTurnOptions.get(item);
            if (associatedOption != null) {
                item.setEnabled(actions.contains(associatedOption));
            }
        }
    }


}
