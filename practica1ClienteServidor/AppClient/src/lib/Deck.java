package lib;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> deckCards;
    private String[] Suits; //Clubs, Diamonds, Hearts, Spades
    private String[] Ranks;
    private int count;

    public Deck() {
        String[] suits = {"C", "D", "H", "S"};
        String[] ranks = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

        this.setSuits(suits);
        this.setRanks(ranks);

        //Creates the entire dack
        this.deckCards = new ArrayList<Card>();
        for (String suit : Suits) {
            for (String rank : Ranks) {
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
        return Suits;
    }

    public void setSuits(String[] suits) {
        Suits = suits;
    }

    public String[] getRanks() {
        return Ranks;
    }

    public void setRanks(String[] ranks) {
        Ranks = ranks;
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
