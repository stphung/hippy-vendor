package org.stphung.vending;

import java.text.DecimalFormat;

/**
 * Represents an item that is being sold in a shop.
 */
public class ShopEntry {
    private static final DecimalFormat FORMATTER = new DecimalFormat("#,###");

    private final String name;
    private final int count;
    private final long price;

    public ShopEntry(String name, int count, long price) {
        this.name = name;
        this.count = count;
        this.price = price;
    }

    public String getName() {
        return this.name;
    }

    public int getCount() {
        return this.count;
    }

    public long getPrice() {
        return this.price;
    }

    @Override
    public String toString() {
        return "ShopEntry{" +
                "name='" + name + '\'' +
                ", count='" + count + '\'' +
                ", price=" + price +
                '}';
    }
}
