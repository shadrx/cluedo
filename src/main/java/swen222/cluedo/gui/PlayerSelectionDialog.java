package swen222.cluedo.gui;

import swen222.cluedo.model.Player;

import javax.swing.*;
import java.awt.*;

public class PlayerSelectionDialog extends JDialog {

    public interface PlayerSelectionDelegate {
        Player playerSelectionDelegateDidCreatePlayer(PlayerSelectionDialog dialog);
    }

    //Adapted from http://docs.oracle.com/javase/tutorial/uiswing/examples/layout/SpringDemo4Project/src/layout/SpringDemo4.java

    private PlayerSelectionDelegate _delegate;

    public PlayerSelectionDialog(PlayerSelectionDelegate delegate) {
        _delegate = delegate;

        SpringLayout layout = new SpringLayout();
        getContentPane().setLayout(layout);

        //Create and add the components.
        JLabel label = new JLabel("Name: ");
        JTextField textField = new JTextField("Enter your name.", 15);
        getContentPane().add(label);
        getContentPane().add(textField);

        //Adjust constraints for the label so it's at (5,5).
        SpringLayout.Constraints  labelCons =
                layout.getConstraints(label);
        labelCons.setX(Spring.constant(5));
        labelCons.setY(Spring.constant(5));

        //Adjust constraints for the text field so it's at
        //(<label's right edge> + 5, 5).
        SpringLayout.Constraints textFieldCons =
                layout.getConstraints(textField);
        textFieldCons.setX(Spring.sum(Spring.constant(5),
                labelCons.getConstraint(SpringLayout.EAST)));
        textFieldCons.setY(Spring.constant(5));

        //Adjust constraints for the content pane.
        setContainerSize(getContentPane(), 5);

        //Display the window.
        this.pack();
        this.setVisible(true);
    }


    public static void setContainerSize(Container parent, int pad) {
        SpringLayout layout = (SpringLayout) parent.getLayout();
        Component[] components = parent.getComponents();
        Spring maxHeightSpring = Spring.constant(0);
        SpringLayout.Constraints pCons = layout.getConstraints(parent);

        //Set the container's right edge to the right edge
        //of its rightmost component + padding.
        Component rightmost = components[components.length - 1];
        SpringLayout.Constraints rCons =
                layout.getConstraints(rightmost);
        pCons.setConstraint(
                SpringLayout.EAST,
                Spring.sum(Spring.constant(pad),
                        rCons.getConstraint(SpringLayout.EAST)));

        //Set the container's bottom edge to the bottom edge
        //of its tallest component + padding.
        for (int i = 0; i < components.length; i++) {
            SpringLayout.Constraints cons =
                    layout.getConstraints(components[i]);
            maxHeightSpring = Spring.max(maxHeightSpring,
                    cons.getConstraint(
                            SpringLayout.SOUTH));
        }
        pCons.setConstraint(
                SpringLayout.SOUTH,
                Spring.sum(Spring.constant(pad), maxHeightSpring));
    }
}
