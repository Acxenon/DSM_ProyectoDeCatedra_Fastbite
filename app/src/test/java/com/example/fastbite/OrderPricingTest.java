package com.example.fastbite;

import com.example.fastbite.domain.OrderPricing;
import org.junit.Test;
import static org.junit.Assert.*;

public class OrderPricingTest {
    @Test public void quantitiesAndDelivery() {
        int subtotal = OrderPricing.subtotal(new int[]{650, 200}, new int[]{2, 1});
        assertEquals(1500, subtotal);
        assertEquals(1750, OrderPricing.total(subtotal, false));
    }
    @Test public void couponOnlyDiscountsFood() {
        assertEquals(150, OrderPricing.discount(1500, true));
        assertEquals(1600, OrderPricing.total(1500, true));
    }
    @Test public void discountRoundsToNearestCent() {
        assertEquals(11, OrderPricing.discount(105, true));
        assertEquals(10, OrderPricing.discount(104, true));
    }
    @Test public void emptyCartHasNoDelivery() {
        assertEquals(0, OrderPricing.subtotal(new int[]{}, new int[]{}));
        assertEquals(0, OrderPricing.total(0, false));
    }
    @Test(expected = IllegalArgumentException.class) public void rejectsOverLimit() {
        OrderPricing.subtotal(new int[]{650}, new int[]{21});
    }
    @Test(expected = IllegalArgumentException.class) public void rejectsZeroQuantity() {
        OrderPricing.subtotal(new int[]{650}, new int[]{0});
    }
    @Test(expected = IllegalArgumentException.class) public void rejectsNegativePrice() {
        OrderPricing.subtotal(new int[]{-1}, new int[]{1});
    }
    @Test(expected = IllegalArgumentException.class) public void rejectsMismatchedArrays() {
        OrderPricing.subtotal(new int[]{650}, new int[]{});
    }
}
