package main.java.com.model;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> deckCards;
    private String[] suits; //Clubs, Diamonds, Hearts, Spades
    private String[] ranks;
    private int count;

    public Deck() {
        String[] suits = {"C", "D", "H", "S"};
        String[] ranks = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

        this.setSuits(suits);
        this.setRanks(ranks);

        //Creates the entire dack
        this.deckCards = new ArrayList<Card>();
        for (String suit : this.suits) {
            for (String rank : this.ranks) {
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

    public String[] getSuits() {
        return suits;
    }

    public void setSuits(String[] suits) {
        this.suits = suits;
    }

    public String[] getRanks() {
        return ranks;
    }

    public void setRanks(String[] ranks) {
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
    public void deal(Hand playerHand, boolean show) {
        Card dealtCard = this.deckCards.get(0);
        this.deckCards.remove(0);
        playerHand.take(dealtCard, show);
        this.setCount(this.deckCards.size());
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
