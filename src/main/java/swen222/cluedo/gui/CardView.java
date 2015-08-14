package swen222.cluedo.gui;

import swen222.cluedo.model.card.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CardView extends JPanel implements MouseListener {

    public interface CardListener {
        void cardViewDidSelectCard(CardView cardView, Card card);
    }

    private static final int CardMargin = 20;
    private static final int PreferredHeight = 220;

    private List<Card> _cards;
    private List<Image> _cardImages;
    private Integer[] _imageXs;

    private Optional<CardListener> _cardListener = Optional.empty();

    public CardView(List<Card> cards) {
        this.setCards(cards);

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
        return new Dimension((int)width, PreferredHeight);
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

}
