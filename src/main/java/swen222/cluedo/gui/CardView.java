package swen222.cluedo.gui;

import com.sun.xml.internal.ws.util.StreamUtils;
import swen222.cluedo.model.card.Card;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Optional;
import java.util.stream.Stream;

public class CardView extends JPanel implements MouseListener {

    public interface CardListener {
        void cardViewDidSelectCard(CardView cardView, Card card);
    }

    private static final int CardMargin = 20;
    private static final int PreferredHeight = 400;

    private Stream<Card> _cards;
    private Stream<Image> _cardImages;
    private Stream<Integer> _imageXs;

    private Optional<CardListener> _cardListener;

    public CardView(Stream<Card> cards) {
        this.setCards(cards);

        this.addMouseListener(this);
    }

    public void setCards(Stream<Card> cards) {
        _cards = cards;
        _cardImages = cards.map(Card::cardImage);


        final int[] x = {0}; //This is a one-element array because apparently you need to do that to modify the variable in a lambda.
                            //I know, it seems crazy to me too.

        _imageXs = _cardImages.map((image) -> {
            int imageWidth = image.getWidth(null) * PreferredHeight / image.getHeight(null);
            int retVal = x[0];
            x[0] += imageWidth * CardMargin;
            return retVal;
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Stream<swen222.cluedo.StreamUtils.Tuple<Image, Integer>> tuples = swen222.cluedo.StreamUtils.zip(_cardImages, _imageXs);
        tuples.forEachOrdered((tuple) -> {
            Image image = tuple.a;
            int imageWidth = image.getWidth(null) * PreferredHeight / image.getHeight(null);
            g.drawImage(image, tuple.b, 0, imageWidth, PreferredHeight, null);
        });

    }

    @Override
    public Dimension getPreferredSize() {
        long width = CardMargin * (_cards.count() - 1);
        width = _cardImages.reduce(width, (sum, image) -> sum + image.getWidth(null) * PreferredHeight / image.getHeight(null), Long::sum);
        return new Dimension((int)width, PreferredHeight);
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
