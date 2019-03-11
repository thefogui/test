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
        this.askBet();
    }

    public void askPlayer() {
        System.out.println();
		System.out.println("Do you want another card? (Y/N)");
		String choice = input.nextLine();
		if (choice.toUpperCase().equals("Y") ){
			roundCount += 1;
			theDeck.deal(playerHand, true);
			if (playerHand.netValue > 21){
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
        try {
            playerBet = Integer.valueOf(this.playerBet).intValue();
        } catch(NumberFormatException e) {
            throws e;
        }
    }

    public void dealerDraws() {
        if (dealerHand.netValue > 21){
			System.out.println("Dealer's hand went over 21!");
			playerWins();
		} else if (dealerHand.netValue <= 16){
			theDeck.deal(dealerHand, true);
		} else {
			System.out.println("Dealer stands.");
			if (playerHand.netValue < dealerHand.netValue) {
				dealerWins();
			} else if (playerHand.netValue > dealerHand.netValue){
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
		dealerHand.hiddenDisplay();
		playerHand.display();
    }

    public void endReveal() {
        System.out.println();
		System.out.println("RESULTS:");
		dealerHand.display();
		playerHand.display();
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
		System.out.println(nameOfPlayer + ", you win!");
		playerMoney += playerBet;
		gameOver=true;
    }
}
