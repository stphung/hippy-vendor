package org.stphung.cart;

public class CartItem {
    private final String name;
    private final int count;

    public CartItem(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "name='" + name + '\'' +
                ", count='" + count + '\'' +
                '}';
    }
}
