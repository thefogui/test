package main.java.com.model;

import java.util.HashMap;

/**
 * Function used to save all clients that are connected or connected before in the server
 */
public class Users {
    private HashMap<Integer, BlackJack> users;

    public Users () {
        this.users = new HashMap<Integer, BlackJack>();
    }

    /**
     * Add a new user to the hashMap, and check if the user has enough cash to keep playing
     * @param userId the user Id to insert or check
     * @exception NullPointerException that indicates the player does not exist,
     */
    public void addNewUser(int userId) throws NullPointerException {
        try {
            synchronized (this.users) {
                BlackJack blackJack = this.users.get(userId);

                if (blackJack == null) {
                    this.users.put(userId, new BlackJack(userId));
                } else {
                    if (blackJack.getPlayerHand().getActualValue() < 100) {
                        System.out.println("Not enought money");
                    } else {

                    }
                }
            }
        } catch (NullPointerException ex) {
            throw ex;
        }
    }
}
