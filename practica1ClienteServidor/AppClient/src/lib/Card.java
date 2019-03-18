package lib;

public class Card {
    private int value;
    private char cardNaipe;
    private char rank;

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

    public void setCardNaipe(char cardNaipe) {
        this.cardNaipe = cardNaipe;
    }

    public char getRank() {
        return rank;
    }

    public void setRank(char rank) {
        this.rank = rank;
    }

    /*
     * Function that calculates the value of the actual card
     * return the integer value of the card
     * */
    private int calculateValue() {
        if (this.rank == 'A')
            return 1; //This will be specified 1 or 11 in Hand.java
        else if (this.rank == 'K' || this.rank == 'J' || this.rank == 'Q')
            return 10;

        return Character.getNumericValue(this.rank); //Integer.valueOf(rank).intValue()
    }

    @Override
    public String toString() {
        String suit = "";
        if (this.getCardNaipe()=='H')
            suit = "\\U+2665";
        else if(this.getCardNaipe()=='D')
            suit = "\\U+2666";
        else if(this.getCardNaipe()=='C')
            suit = "\\U+2663";
        else if(this.getCardNaipe()=='S')
            suit = "\\U+2660";
        return suit + Character.toString(this.rank);
    }
}