package test.lib;

import static org.junit.jupiter.api.Assertions.*;
import lib.Card;

public class CardTest {
    @org.junit.jupiter.api.Test
    void getValue() {
        Card card = new Card('S', 'X');
        assertEquals(10, card.getValue());

        card.setRank('A');
        assertEquals(1, card.getValue());

        card.setRank('2');
        assertEquals(2, card.getValue());

        card.setRank('3');
        assertEquals(3, card.getValue());

        card.setRank('4');
        assertEquals(4, card.getValue());

        card.setRank('5');
        assertEquals(5, card.getValue());

        card.setRank('6');
        assertEquals(6, card.getValue());

        card.setRank('7');
        assertEquals(7, card.getValue());

        card.setRank('8');
        assertEquals(8, card.getValue());
    }
}
