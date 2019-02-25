package lib;

public class Card {
    private int value;
    private String cardNaipe;
    private String rank;

    public Card(String cardnNaipe, String rank) {
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

    public String getCardNaipe() {
        return cardNaipe;
    }

    public void setCardNaipe(String cardNaipe) {
        this.cardNaipe = cardNaipe;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    /*
    * Function that calculates the value of the actual card
    * return the integer value of the card
    * */
    private int calculateValue() {
        if (this.cardNaipe.equals("A"))
            return 1; //This will be specified 1 or 11 in Hand.java
        else if (this.cardNaipe.equals("K") || this.cardNaipe.equals("J") || this.cardNaipe.equals("Q"))
            return 10;

        return Integer.parseInt(this.rank); //Integer.valueOf(rank).intValue()
    }

    @Override
    public String toString() {
        return this.getCardNaipe() + this.getValue();
    }
}