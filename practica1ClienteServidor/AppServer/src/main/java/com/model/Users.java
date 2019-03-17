package main.java.com.model;

import java.util.HashMap;

public class Users {
    private HashMap<Integer, BlackJack> users;

    public Users () {
        this.users = new HashMap<Integer, BlackJack>();
    }

    public void addNewUser (int userId) {
        try {
            synchronized (this.users) {
                BlackJack blackJack = this.users.get(userId);

                if (blackJack == null) {
                    this.users.put(userId, new BlackJack(userId));
                } else {
                    //check user money
                }
            }
        } catch (NullPointerException ex) {

        }
    }
}
