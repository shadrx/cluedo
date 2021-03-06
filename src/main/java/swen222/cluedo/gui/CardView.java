package swen222.cluedo.gui;

import swen222.cluedo.model.card.Card;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * CardView is a component that is used to display different Card instances.
 * The cards can be modified without reinstantiating the view.
 * Its size is locked to the size of its cards.
 * Often, it makes sense to embed this component within a JScrollPane (as in the CluedoFrame).
 * Optionally, it can notify a delegate when one of its cards is clicked.
 */
public class CardView extends JPanel implements MouseListener {

    private static final int CardMargin = 20;
    private static final int PreferredHeight = 220;
    private static final long MinimumWidth = (long)(0.6 * PreferredHeight); //width is 0.6 times the height
    private List<Card> _cards;
    private List<Image> _cardImages;
    private Integer[] _imageXs;
    private Optional<CardSelectionDelegate> _cardListener = Optional.empty();

    public CardView(List<Card> cards, Optional<CardSelectionDelegate> cardListener) {
        this.setCards(cards);

        _cardListener = cardListener;

        this.addMouseListener(this);
    }

    public void setCards(List<Card> cards) {
        _cards = cards;
        _cardImages = cards.stream().map(Card::cardImage).collect(Collectors.toList());

        _imageXs = new Integer[_cardImages.size()];
        int x = 0;

        for (int i = 0; i < _cardImages.size(); i++) {
            Image image = _cardImages.get(i);
            _imageXs[i] = x;

            int imageWidth = image.getWidth(null) * PreferredHeight / image.getHeight(null);
            x += imageWidth + CardMargin;
        }

        this.invalidate();
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        for (int i = 0; i < _cardImages.size(); i++) {
            Image image = _cardImages.get(i);
            int imageWidth = image.getWidth(null) * PreferredHeight / image.getHeight(null);
            g.drawImage(image, _imageXs[i], 0, imageWidth, PreferredHeight, null);
        }

    }

    @Override
    public Dimension getPreferredSize() {
        long width = CardMargin * (_cards.size() - 1);
        width = _cardImages.stream().reduce(width, (sum, image) -> sum + image.getWidth(null) * PreferredHeight / image.getHeight(null), Long::sum);

        long constrainedWidth = Math.max(width, MinimumWidth); //Set a minimum width if there are no cards.

        return new Dimension((int) constrainedWidth, PreferredHeight);
    }

    @Override
    public Dimension getMinimumSize() {
        return this.getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return this.getPreferredSize();
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if (!_cardListener.isPresent()) {
            return;
        }

        int x = e.getX();

        for (int i = 0; ; i++) {
            Image image = _cardImages.get(i);
            int imageWidth = image.getWidth(null) * PreferredHeight / image.getHeight(null);

            int cardX = _imageXs[i];
            if (x >= cardX && x <= cardX + imageWidth) {
                _cardListener.get().cardViewDidSelectCard(this, _cards.get(i));
                return;
            }
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public interface CardSelectionDelegate {
        void cardViewDidSelectCard(CardView cardView, Card card);
    }

}
