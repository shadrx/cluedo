package swen222.cluedo.gui;

import javax.swing.*;
import java.awt.*;

public class CluedoFrame extends JFrame {

    public GameCanvas canvas;

    public CluedoFrame() {
        super("Cluedo");
        this.canvas = new GameCanvas();
        this.setLayout(new BorderLayout());
        this.add(canvas, BorderLayout.CENTER);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.pack();
        this.setResizable(false);
        this.setVisible(true);
    }
}
