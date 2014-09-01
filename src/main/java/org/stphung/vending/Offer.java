package org.stphung.vending;

import java.util.List;

public class Offer {
    private final String id;
    private final List<ShopEntry> shopEntries;

    public Offer(String id, List<ShopEntry> shopEntries) {
        this.id = id;
        this.shopEntries = shopEntries;
    }

    public String getId() {
        return this.id;
    }

    public List<ShopEntry> getShopEntries() {
        return this.shopEntries;
    }

    @Override
    public String toString() {
        return "Offer{" +
                "id='" + id + '\'' +
                ", shopEntries=" + shopEntries +
                '}';
    }
}
