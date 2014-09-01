package org.stphung.vending;

/**
 * Represents an item in a vendor's cart.
 */
public class CartItem {
    private final String name;
    private final int count;

    public CartItem(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return this.name;
    }

    public int getCount() {
        return this.count;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "name='" + this.name + '\'' +
                ", count='" + this.count + '\'' +
                '}';
    }
}
