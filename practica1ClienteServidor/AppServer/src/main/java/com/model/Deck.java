package main.java.com.model;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> deckCards;
    private char[] suits; //Clubs, Diamonds, Hearts, Spades
    private char[] ranks;
    private int count;

    public Deck() {
        char[] suits = {'C', 'D', 'H', 'S'}; //Ascii code 03, 04, 03, 06
        char[] ranks = {'A', '2', '3', '4', '5', '6', '7', '8', '9', 'X','J', 'Q', 'K'};

        this.setSuits(suits);
        this.setRanks(ranks);

        //Creates the entire dack
        this.deckCards = new ArrayList<Card>();
        for (char suit : this.suits) {
            for (char rank : this.ranks) {
                deckCards.add(new Card(suit, rank));
            }
        }
        count = this.deckCards.size();

    }

    public ArrayList<Card> getDeck() {
        return this.deckCards;
    }

    public void setDeck(ArrayList<Card> deckContents) {
        this.deckCards = deckContents;
    }

    public char[] getSuits() {
        return suits;
    }

    public void setSuits(char[] suits) {
        this.suits = suits;
    }

    public char[] getRanks() {
        return ranks;
    }

    public void setRanks(char[] ranks) {
        this.ranks = ranks;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    /*
     * Function that deal the cards for the player
     * */
    public Card deal(Hand playerHand) {
        Card dealtCard = this.deckCards.get(0);
        this.deckCards.remove(0);
        playerHand.take(dealtCard);
        this.setCount(this.deckCards.size());
        return dealtCard;
    }

    /*
     * Function to shuffle the deck
     * */
    public void shuffle() {
        Collections.shuffle(this.deckCards);
    }

    @Override
    public String toString() {
        return this.deckCards.toString();
    }
}
