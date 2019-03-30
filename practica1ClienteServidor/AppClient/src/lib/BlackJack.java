/*
 * Class that refers to the blackJack
 * Can save the player and the dealer hand
 *
 * Authors: Vitor Carvalho AND Ivet Aymerich
 */

package lib;

public class BlackJack {
    private Hand dealerHand;
    private Hand playerHand;
    private int playerMoney;
    private int playerBet;
    private int playerName;
    private Boolean gameOver;
    private int roundCount;
    private boolean isRunning;
    public static final int MAX_BET = 100;

    /*
     * Constructor of the class
     */
    public BlackJack(int playerName) {
        this.playerName = playerName;
        this.playerHand = new Hand(String.valueOf(this.playerName));
        this.dealerHand = new Hand("Dealer");
        this.playerMoney = this.playerHand.getCash();
        this.gameOver = false;
        this.playerBet = MAX_BET;
        this.roundCount = 0;
        this.isRunning = true;
    }

    public void setRunning(boolean running){
        this.isRunning = running;
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

    public int getPlayerMoney() {
        return playerMoney;
    }

    public void setPlayerMoney(int playerMoney) {
        this.playerMoney = playerMoney;
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
}