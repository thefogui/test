package main.java.com.model;

import java.util.ArrayList;

/**
 * It represents the hand of the player
 *
 * @author Vitor Carvalho and Ivet Aymerich
 */
public class Hand {
    private String player;
    private ArrayList<Card> handCards;
    private int actualValue;
    private int cash;

    /**
     *
     * @param player the id of the player, an integer value
     */
    public Hand(String player) {
        this.player = player;
        this.handCards = new ArrayList<Card>();
        this.actualValue = 0; //the hand start empty
        this.cash = 500;
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

    /**
     * This function return the actual value of the card
     *
     * @param card that is a instance of a Card class
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

    /**
     * Function that allows the player take a card
     *
     * */
    public void take(Card card) {
        this.handCards.add(card);
        this.actualValue += getActualValueByCard(card);
    }

    /**
     * Function to add a card to a player hand
     * @param rank the rank of the card, a char value
     * @param suit the  suit of the card, a char value
     */
    public void take(char rank, char suit) {
        Card card = new Card(suit, rank);
        this.handCards.add(card);
        this.actualValue += getActualValueByCard(card);
    }

    /**
     * This function return a boolean value that indicates if the player has a blackjack
     * @return return true if the player has a blackjack, false otherwise.
     */
    public boolean getblack() {
        if (this.getHandCards().get(0).getRank() == 'A' && (
                this.getHandCards().get(1).getRank() == 'K' ||
                this.getHandCards().get(1).getRank() == 'Q' ||
                this.getHandCards().get(1).getRank() == 'J'))
            return true;
        return (this.getHandCards().get(1).getRank() == 'A' && (
                    this.getHandCards().get(0).getRank() == 'K' ||
                    this.getHandCards().get(0).getRank() == 'Q' ||
                    this.getHandCards().get(0).getRank() == 'J'));
    }
}
