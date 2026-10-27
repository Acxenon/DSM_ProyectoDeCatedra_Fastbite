package com.example.fastbite.domain;

/** Todos los importes se expresan en centavos para evitar errores de decimales. */
public final class OrderPricing {
    private OrderPricing() {}
    public static int subtotal(int[] prices, int[] quantities) {
        if (prices.length != quantities.length) throw new IllegalArgumentException("Datos incompletos");
        long total = 0;
        for (int i = 0; i < prices.length; i++) {
            if (prices[i] < 0 || quantities[i] < 1 || quantities[i] > 20)
                throw new IllegalArgumentException("Precio o cantidad no válido");
            total += (long) prices[i] * quantities[i];
        }
        if (total > Integer.MAX_VALUE) throw new IllegalArgumentException("Pedido demasiado grande");
        return (int) total;
    }
    public static int discount(int subtotal, boolean firstOrderCoupon) {
        if (subtotal < 0) throw new IllegalArgumentException("Subtotal negativo");
        return firstOrderCoupon ? (int) (((long) subtotal + 5) / 10) : 0;
    }
    public static int delivery(int subtotal) { return subtotal > 0 ? 250 : 0; }
    public static int total(int subtotal, boolean firstOrderCoupon) {
        return Math.addExact(subtotal - discount(subtotal, firstOrderCoupon), delivery(subtotal));
    }
}
