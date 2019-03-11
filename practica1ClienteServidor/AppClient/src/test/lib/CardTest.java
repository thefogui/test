package test.lib;

import static org.junit.jupiter.api.Assertions.*;
import lib.Card;

public class CardTest {
    @org.junit.jupiter.api.Test
    void getValue() {
        Card cardtesting = new Card("A", "1");
        assertEquals(1, cardtesting.getValue());

        Card cardtesting2 = new Card("C", "4");
        assertEquals(4, cardtesting2.getValue());

        Card cardtesting3 = new Card("K", "10");
        assertEquals(10, cardtesting3.getValue());
    }
}
