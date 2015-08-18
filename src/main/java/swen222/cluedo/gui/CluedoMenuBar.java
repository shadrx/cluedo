package swen222.cluedo.gui;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class CluedoMenuBar extends JMenuBar {

    private JMenu _fileMenu;

    public CluedoMenuBar() {
        setupFileMenu();
    }

    private void setupFileMenu() {
        _fileMenu = new JMenu("File");
        this.add(_fileMenu);

        addMenuItemToMenu(_fileMenu, "Quit", e -> System.exit(0), KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
    }


    /**
     * Adds a menu item to the cluedo menu bar
     *
     * @param menu the menu to add the item to
     * @param name the name of the menu item
     * @param al the action to be performed when the menu item is used
     * @param keyStroke a key stroke to be used as a shortcut
     */
    private void addMenuItemToMenu(JMenu menu, String name, ActionListener al, KeyStroke keyStroke) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.setAccelerator(keyStroke);
        menuItem.addActionListener(al);
        menu.add(menuItem);
    }


}
