package main.java.com.model;

/**
 * Represents a card
 *
 * @author Vitor Carvalho and Ivet Aymerich
 */
public class Card {
    private int value;
    private char cardNaipe;
    private char rank;

    /**
     * Creates a single card
     * @param cardNaipe the card suit, a char value
     * @param rank the rank of card, a char value
     */
    public Card(char cardNaipe, char rank) {
        this.cardNaipe = cardNaipe;
        this.rank = rank;

        this.value = this.calculateValue(); //calculate the value of the card
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public char getCardNaipe() {
        return cardNaipe;
    }

    /**
     * Returns the card suit based on the protocol
     *
     * @return a char value that cab ve 0x03, 0x04, 0x04 or 0x05
     */
    public char getCardProtcolNaipe() {
        char naipe;
        if (this.cardNaipe == 'C')
            naipe = (char) 0x03;
        else if (this.cardNaipe == 'D')
            naipe = (char) 0x04;
        else if (this.cardNaipe == 'H')
            naipe = (char) 0x05;
        else
            naipe = (char) 0x06;
        return naipe;
    }

    public void setCardNaipe(char cardNaipe) {
        this.cardNaipe = cardNaipe;
    }

    public char getRank() {
        return rank;
    }

    public void setRank(char rank) {
        this.rank = rank;
    }

    /**
     * Function that calculates the value of the actual card
     * return the integer value of the card
     * */
    private int calculateValue() {
        if (this.rank == 'A')
            return 1; //This will be specified 1 or 11 in Hand.java
        else if (this.rank == 'K' || this.rank == 'J' || this.rank == 'Q' || this.rank == 'X')
            return 10;

        return Character.getNumericValue(this.rank); //Integer.valueOf(rank).intValue()
    }

    @Override
    public String toString() {
        String suit = "";
        String rank = "";
        if (this.cardNaipe=='H')
            suit = "\u2665";
        else if(this.cardNaipe=='D')
            suit = "\u2666";
        else if(this.cardNaipe=='C')
            suit = "\u2663";
        else if(this.cardNaipe=='S')
            suit = "\u2660";

        if (this.rank == 'X')
            rank = "10";
        else
            rank = Character.toString(this.rank);
        return  rank + suit;
    }
}