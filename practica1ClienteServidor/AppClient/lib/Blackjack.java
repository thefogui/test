package lib;

public class Blackjack {
    private Deck deck;
    private Hand dealerHand;
    private Hand playerHand;
    private int playerMoney;
    private int playerBet;
    private String playerName;

    public Blackjack(String playerName) {
        this.deck = new Deck();
        this.deck.shuffle(); //shuffle the deck
        this.playerName = playerName;
        this.dealerHand = new Hand(this.playerName);
        this.dealerHand = new Hand("Dealer");
        this.playerMoney = 10;
        this.playerBet = 1;
    }

    public void startPlay() {

    }

    public void askPlayer() {

    }

    public void askBet() {

    }

    public void dealerDraws() {

    }

    public void reveal() {

    }

    public void endReveal() {

    }

    public void dealerWins() {

    }

    public void playerWins() {

    }
}
