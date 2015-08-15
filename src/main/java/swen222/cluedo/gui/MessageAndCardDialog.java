package swen222.cluedo.gui;

import swen222.cluedo.model.card.Card;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class MessageAndCardDialog extends JDialog {
    public final String message;
    public final List<Card> cards;

    private static final int VerticalGap = 10;
    public MessageAndCardDialog(Frame parent, String title, String message, List<Card> cards, CardView.CardListener cardListener) {
        super(parent, title, true);
        this.message = message;
        this.cards = cards;
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        getContentPane().add(Box.createVerticalStrut(VerticalGap));


        JTextArea label = new JTextArea();
        label.setText(message);
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

        CardView cardView = new CardView(cards, Optional.ofNullable(cardListener));
        Dimension cardViewSize = cardView.getMaximumSize();
        cardView.setAlignmentX(Component.CENTER_ALIGNMENT);


        getContentPane().add(cardView);

        getContentPane().add(Box.createVerticalStrut(VerticalGap));

        JButton closeButton = new JButton("Close");
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener((actionEvent) -> {
            this.dispose();
        });
        getContentPane().add(closeButton);

        getContentPane().add(Box.createVerticalStrut(VerticalGap));

        //TODO This works, but it's by no means good.
        this.setMinimumSize(new Dimension(cardViewSize.width + 30, cardViewSize.height + label.getPreferredSize().height + closeButton.getPreferredSize().height + 4 * VerticalGap + 50));

        this.setResizable(false);
        this.pack();
        this.setVisible(true);
    }
}
