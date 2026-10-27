package com.example.fastbite;

import com.example.fastbite.model.OrderLine;
import com.example.fastbite.model.FoodOrder;

import org.junit.Test;
import java.util.Arrays;
import static org.junit.Assert.*;

public class OrderModelTest {
    @Test public void orderSurvivesJsonRoundTripWithHistoricalPrices() {
        OrderLine line = new OrderLine("burger_classic", "Nombre histórico", "🍔", 625, 2);
        FoodOrder order = new FoodOrder("order-1", "user-1", "Ana López", Arrays.asList(line),
            "Casa 12, San Salvador", "76543210", "Sin cebolla", 1250, 125, 250, 1375,
            1000L, 1001L, "PENDING", false);
        FoodOrder restored = FoodOrder.Companion.from(order.json());
        assertEquals("Ana López", restored.getCustomer());
        assertEquals("user-1", restored.getOwnerId());
        assertEquals(625, restored.getLines().get(0).getPrice());
        assertEquals("Nombre histórico", restored.getLines().get(0).getName());
        assertEquals(1375, restored.getTotal());
        assertEquals("Sin cebolla", restored.getNotes());
        assertFalse(restored.getSynced());
    }
    @Test public void cloudPayloadDoesNotIncludeLocalSyncFlag() {
        FoodOrder order = new FoodOrder("order-2", "user-1", "Ana López",
            Arrays.asList(new OrderLine("water", "Agua", "💧", 100, 1)),
            "Casa 12, San Salvador", "76543210", "", 100, 0, 250, 350,
            1000L, 1001L, "CANCELLED", true);
        assertFalse(order.cloud().containsKey("synced"));
        assertEquals("CASH", order.cloud().get("payment"));
        assertEquals("CANCELLED", FoodOrder.Companion.from(order.json()).getStatus());
    }
}
