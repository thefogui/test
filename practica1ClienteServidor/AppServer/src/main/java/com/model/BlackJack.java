package main.java.com.model;

import java.util.ArrayList;

public class BlackJack {
    private Deck deck;
    private Hand dealerHand;
    private Hand playerHand;
    private int playerBet;
    private int playerName;
    private Boolean gameOver;
    private int roundCount;
    private boolean isRunning;
    public static final int MAX_BET = 100;

    public BlackJack(int playerName) {
        this.playerName = playerName;
        this.playerHand = new Hand(String.valueOf(this.playerName));
        this.dealerHand = new Hand("Dealer");
        this.gameOver = false;
        this.playerBet = MAX_BET;
        this.roundCount = 0;
        this.isRunning = true;
        this.startGame();
    }

    public void startGame() {
        this.deck = new Deck();
        this.deck.shuffle(); //shuffle the deck
        Card card = this.deck.deal(this.getPlayerHand());
        card = this.deck.deal(this.getPlayerHand());
        card = this.deck.deal(this.getDealerHand());
        card = this.deck.deal(this.getDealerHand());
    }

    public void setRunning(boolean running){
        this.isRunning = running;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public Hand getDealerHand() {
        return dealerHand;
    }

    public void setDealerHand(Hand dealerHand) {
        this.dealerHand = dealerHand;
    }

    public Hand getPlayerHand() {
        return playerHand;
    }

    public void setPlayerHand(Hand playerHand) {
        this.playerHand = playerHand;
    }

    public void setPlayerMoney(int playerMoney) {
        this.getPlayerHand().setCash(playerMoney);
    }

    public int getPlayerBet() {
        return playerBet;
    }

    public void setPlayerBet(int playerBet) {
        this.playerBet = playerBet;
    }

    public int getPlayerName() {
        return playerName;
    }

    public void setPlayerName(int playerName) {
        this.playerName = playerName;
    }

    public Boolean getGameOver() {
        return gameOver;
    }

    public void setGameOver(Boolean gameOver) {
        this.gameOver = gameOver;
    }

    public int getRoundCount() {
        return roundCount;
    }

    public void setRoundCount(int roundCount) {
        this.roundCount = roundCount;
    }

    public int getPlayerID() {
        return this.playerName;
    }

    public boolean getIsRunning() {
        return this.isRunning;
    }

    public Card dealPlayerCard() {
        return this.deck.deal(this.getPlayerHand());
    }

    public void doubleBet() throws Exception {
        int doubleBet = this.playerBet * 2;
        this.playerBet = doubleBet;
    }

    public char getWinner() {
        if (this.dealerHand.getblack())
            return '1';
        else if (this.dealerHand.getActualValue() > 21)
            return '0';
        else if (this.getPlayerHand().getblack())
            return '0';
        else {
            if (playerHand.getActualValue() < dealerHand.getActualValue()) {
                this.getPlayerHand().setCash(this.getPlayerHand().getCash() - this.playerBet);
                return '1';
            } else if (playerHand.getActualValue() > dealerHand.getActualValue()){
                this.getPlayerHand().setCash(this.getPlayerHand().getCash() + this.playerBet);
                return '0';
            } else {
                return '2';
            }
        }
    }

    public void dealerAskCard() {
       while (this.getDealerHand().getActualValue() < 17) {
           Card card = this.deck.deal(this.getDealerHand());
       }
    }

    public void restart() {
        this.getPlayerHand().setHandCards(new ArrayList<>());
        this.dealerHand.setHandCards(new ArrayList<>());
        this.gameOver = false;
        this.playerBet = MAX_BET;
        this.roundCount = 0;
        this.isRunning = true;
        startGame();
    }
}