package com.example.myapplication;

import org.junit.Test;
import static org.junit.Assert.*;

public class ExampleUnitTest {
    @Test public void priceUsesExactMinorUnits() {
        assertEquals(10L, Order.parsePrice("0.10"));
        assertEquals(85000L, Order.parsePrice("850"));
        assertEquals("25.50", Order.formatMoney(2550));
    }
    @Test public void totalSupportsLargeOrdersWithoutIntegerOverflow() {
        Order order = new Order(1, "ORD001", "Customer", "0771234567", "Rice", 9999, 99999999L, "Pending");
        assertEquals(999899990001L, order.getTotalCents());
    }
    @Test public void rejectsInvalidPrices() {
        for (String price : new String[]{"0", "-1", "1.001", "NaN", "1e3", "1000000", ""}) {
            try { Order.parsePrice(price); fail("Accepted invalid price: " + price); }
            catch (IllegalArgumentException expected) { }
        }
    }
    @Test(expected = IllegalArgumentException.class) public void rejectsZeroQuantity() {
        new Order(1, "ORD001", "Customer", "0771234567", "Rice", 0, 100, "Pending");
    }
    @Test(expected = IllegalArgumentException.class) public void rejectsUnknownStatus() {
        new Order(1, "ORD001", "Customer", "0771234567", "Rice", 1, 100, "Unknown");
    }
}
