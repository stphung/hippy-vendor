package org.stphung.price;

import java.util.Date;
import java.util.Optional;

public class ItemPrice {
    private final String name;
    private final int count;
    private final long price;
    private final Optional<Date> dateOptional;

    public ItemPrice(String name, int count, long price, Optional<Date> dateOptional) {
        this.name = name;
        this.dateOptional = dateOptional;
        this.count = count;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public Optional<Date> getDateOptional() {
        return dateOptional;
    }

    public int getCount() {
        return count;
    }

    public long getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "ItemPrice{" +
                "name='" + name + '\'' +
                ", dateOptional=" + dateOptional +
                ", count=" + count +
                ", price='" + price + '\'' +
                '}';
    }
}
