package org.stphung.vending;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.UUID;

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

    public Offer modifyCount(int index, int count) {
        ImmutableList.Builder<ShopEntry> builder = ImmutableList.builder();
        for (int i = 0; i < this.shopEntries.size(); i++) {
            if (index != i) {
                builder.add(this.shopEntries.get(i));
            } else {
                ShopEntry shopEntry = this.shopEntries.get(i);
                ShopEntry newShopEntry = new ShopEntry(shopEntry.getName(), count, shopEntry.getPrice());
                builder.add(newShopEntry);
            }
        }
        return new Offer(UUID.randomUUID().toString(), builder.build());
    }

    public Offer modifyPrice(int index, int price) {
        ImmutableList.Builder<ShopEntry> builder = ImmutableList.builder();
        for (int i = 0; i < this.shopEntries.size(); i++) {
            if (index != i) {
                builder.add(this.shopEntries.get(i));
            } else {
                ShopEntry shopEntry = this.shopEntries.get(i);
                ShopEntry newShopEntry = new ShopEntry(shopEntry.getName(), shopEntry.getCount(), price);
                builder.add(newShopEntry);
            }
        }
        return new Offer(UUID.randomUUID().toString(), builder.build());
    }

    @Override
    public String toString() {
        return "Offer{" +
                "id='" + id + '\'' +
                ", shopEntries=" + shopEntries +
                '}';
    }
}
