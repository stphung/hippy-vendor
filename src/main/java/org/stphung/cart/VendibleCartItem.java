package org.stphung.cart;

import java.text.DecimalFormat;

public class VendibleCartItem extends CartItem {
    private static final DecimalFormat FORMATTER = new DecimalFormat("#,###");

    private final long price;

    public VendibleCartItem(String name, int count, long price) {
        super(name, count);
        this.price = price;
    }

    public long getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return this.getCount() + " " + this.getName() + " @ " + FORMATTER.format(this.getPrice());
    }
}
