package swen222.cluedo.gui;

import swen222.cluedo.model.card.Card;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;

/**
 * A self-displaying dialog that shows a list of cards in a CardView, a message, and (optionally) a close button.
 * The intention is that the view is used for displaying results, in which case the close button is present,
 * or used to select a card from a range of cards, in which case the dialog closes when a card is selected.
 */
public class MessageAndCardDialog extends JDialog {
    private static final int VerticalGap = 10;
    public final String message;
    public final List<Card> cards;

    public MessageAndCardDialog(Frame parent, String title, String message, boolean showCloseButton, List<Card> cards, CardView.CardSelectionDelegate cardSelectionDelegate) {
        super(parent, title, true);
        this.setModalityType(ModalityType.APPLICATION_MODAL);

        this.message = message;
        this.cards = cards;

        this.setDefaultCloseOperation(showCloseButton ? DISPOSE_ON_CLOSE : DO_NOTHING_ON_CLOSE);

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        getContentPane().add(Box.createVerticalStrut(VerticalGap));


        JTextArea label = new JTextArea(); //Format the text area to look like a JLabel
        label.setText(message);            //since we need it to be multiline, and JLabel doesn't have a convenient .numberOfLines = 0 workaround.
        label.setWrapStyleWord(true);
        label.setLineWrap(true);
        label.setOpaque(false);
        label.setEditable(false);
        label.setFocusable(false);
        label.setBackground(UIManager.getColor("Label.background"));
        label.setFont(UIManager.getFont("Label.font"));
        label.setMargin(new Insets(10, 10, 10, 10));

        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        getContentPane().add(label);

        getContentPane().add(Box.createVerticalStrut(VerticalGap));

        CardView cardView = new CardView(cards, Optional.of((view, selectedCard) -> {
            if (cardSelectionDelegate != null) {
                cardSelectionDelegate.cardViewDidSelectCard(view, selectedCard);
                this.dispose();
            }
        }));
        Dimension cardViewSize = cardView.getMaximumSize();
        cardView.setAlignmentX(Component.CENTER_ALIGNMENT);

        getContentPane().add(cardView);

        getContentPane().add(Box.createVerticalStrut(VerticalGap));

        JButton closeButton = null;
        if (showCloseButton) {
            closeButton = new JButton("Close");
            closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            closeButton.addActionListener((actionEvent) -> this.dispose());
            getContentPane().add(closeButton);
        }

        getContentPane().add(Box.createVerticalStrut(VerticalGap));

        //TODO This works, but it's by no means good.
        this.setMinimumSize(new Dimension(cardViewSize.width + 30,
                cardViewSize.height +
                        label.getPreferredSize().height +
                        ((closeButton != null) ? closeButton.getPreferredSize().height : 0) +
                        4 * VerticalGap + 50));

        this.setLocationRelativeTo(parent);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
    }
}
