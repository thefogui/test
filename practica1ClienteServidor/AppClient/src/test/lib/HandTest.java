package test.lib;

import lib.Card;
import lib.Hand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HandTest {
    @Test
    void getActualValue() {
        Hand hand = new Hand("1234");
        Card card = new Card('S', 'X');
        hand.take(card);
        assertEquals(10, hand.getActualValue());

        card.setRank('2');
        hand.take(card);
        assertEquals(12, hand.getActualValue());

        card.setRank('2');
        hand.take(card);
        assertEquals(14, hand.getActualValue());
    }

    @Test
    void showCards() {
        Hand hand = new Hand("1290");
        assertEquals("", hand.showCards(), "Not empty!");

        Card card = new Card('S', 'X');
        hand.take(card);

    }
}