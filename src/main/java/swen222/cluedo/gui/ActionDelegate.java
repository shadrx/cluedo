package swen222.cluedo.gui;

/**
 * ActionDelegate is used by both the CluedoMenuBar and the CluedoActionView to notify an (optional) delegate of events.
 */
public interface ActionDelegate {
    void makeAnAccusation();
    void makeASuggestion();
    void endTurn();
}