/*
 * This class is used to save the hand of the players and
 * calculate the total of points of it.
 *
 * Authors: Vitor Carvalho and Ivet Aymerich
 */

package lib;

import java.util.ArrayList;

public class Hand {
    private String player;
    private ArrayList<Card> handCards;
    private int actualValue;
    private int cash;

    public Hand(String player) {
        this.player = player;
        this.handCards = new ArrayList<Card>();
        this.actualValue = 0; //the hand start empty
        this.cash = 0;
    }

    public int getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public ArrayList<Card> getHandCards() {
        return handCards;
    }

    public void setHandCards(ArrayList<Card> handCards) {
        this.handCards = handCards;
    }

    public int getActualValue() {
        return actualValue;
    }

    public void setActualValue(int actualValue) {
        this.actualValue = actualValue;
    }

    public String showCards() {
        return this.handCards.toString();
    }

    /*
     * This function return the actual value of the card
     * @param Card the actual card
     * */
    private int getActualValueByCard(Card card) {
        //if the player has 10 or less scored the value of the card A is equal to 11
        //otherwise the value of the card A is 1
        // for others cards we only call the function of the class card
        if (card.getRank() == 'A') {
            if (actualValue <= 10)
                return 11;
            else
                return 1;
        }
        return card.getValue();
    }

    /*
     * Function that allows the player take a card
     * @param Card the actual card
     * */
    public void take(Card card) {
        this.handCards.add(card);
        this.actualValue += getActualValueByCard(card);
    }
}