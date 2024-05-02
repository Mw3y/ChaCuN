package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Base32Test {

    @Test
    public void isValidWorks() {
        assertTrue(Base32.isValid("ABCDEF"));
        assertFalse(Base32.isValid("ABCDEF1"));
    }

    @Test
    public void encodeBits5Works() {
        assertEquals("A", Base32.encodeBits5(0));
        assertEquals("B", Base32.encodeBits5(1));
        assertEquals("7", Base32.encodeBits5(31));
    }

    @Test
    public void encodeBits10Works() {
        // assertEquals("AA", Base32.encodeBits10(0));
        // assertEquals("AB", Base32.encodeBits10(1));
        assertEquals("BB", Base32.encodeBits10(33));
    }

    @Test
    public void decodeWorks() {
        assertEquals(0, Base32.decode("A"));
        assertEquals(1, Base32.decode("B"));
        assertEquals(31, Base32.decode("7"));
    }

}
