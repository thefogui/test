package lib;

import java.util.Scanner;

public class BlackJack {
    private Deck deck;
    private Hand dealerHand;
    private Hand playerHand;
    private int playerMoney;
    private int playerBet;
    private String playerName;
    private Boolean gameOver;
    private int roundCount;

    public BlackJack(String playerName) {
        this.deck = new Deck();
        this.deck.shuffle(); //shuffle the deck
        this.playerName = playerName;
        this.dealerHand = new Hand(this.playerName);
        this.dealerHand = new Hand("Dealer");
        this.playerMoney = 10;
        this.gameOver = false;
        this.playerBet = 1;
        this.roundCount = 0;
    }

    public void startPlay() {
        this.askBet();
    }

    public boolean isRunning() {
    	return true;
	}

    public void askPlayer(Scanner input) {
        System.out.println();
		//ask for more cards
		String choice = input.nextLine();
		if (choice.toUpperCase().equals("Y") ){
			roundCount += 1;
			this.deck.deal(playerHand, true);
			if (playerHand.getActualValue() > 21){
				System.out.println("Your hand went over 21!");
				dealerWins();
			} else{
				reveal();
			}
		} else if (choice.toUpperCase().equals("N")){
			while(!gameOver){
				dealerDraws();
			}
		} else {
			System.out.println("Pick either Y or N !");
		}
    }

    public void askBet() throws NumberFormatException {
        //read the input

		playerBet = Integer.valueOf(this.playerBet).intValue();

    }

    public void dealerDraws() {
        if (dealerHand.getActualValue() > 21){
			System.out.println("Dealer's hand went over 21!");
			playerWins();
		} else if (dealerHand.getActualValue() <= 16){
			this.deck.deal(dealerHand, true);
		} else {
			System.out.println("Dealer stands.");
			if (playerHand.getActualValue() < dealerHand.getActualValue()) {
				dealerWins();
			} else if (playerHand.getActualValue() > dealerHand.getActualValue()){
				playerWins();
			} else {
				playerWins();
				System.out.println("It's a tie!");
			}
		}
    }

    public void reveal() {
        System.out.println();
		System.out.println("ROUND " + Integer.toString(roundCount) + ":");
		//dealerHand.hiddenDisplay();
		//playerHand.display();
    }

    public void endReveal() {
        System.out.println();
		System.out.println("RESULTS:");
		//dealerHand.display();
		//playerHand.display();
    }

    public void dealerWins() {
        endReveal();
		System.out.println();
		System.out.println("Dealer wins!");
		playerMoney -= playerBet;
		gameOver=true;
    }

    public void playerWins() {
        endReveal();
		System.out.println();
		System.out.println(this.playerHand.getPlayer() + ", you win!");
		playerMoney += playerBet;
		gameOver=true;
    }
}
