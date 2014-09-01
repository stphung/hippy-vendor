package org.stphung.pricing;

import java.util.Date;
import java.util.Optional;

public class ItemData {
    private final String name;
    private final int count;
    private final long price;
    private final Optional<Date> dateOptional;

    public ItemData(String name, int count, long price, Optional<Date> dateOptional) {
        this.name = name;
        this.dateOptional = dateOptional;
        this.count = count;
        this.price = price;
    }

    public String getName() {
        return this.name;
    }

    public Optional<Date> getDateOptional() {
        return this.dateOptional;
    }

    public int getCount() {
        return this.count;
    }

    public long getPrice() {
        return this.price;
    }

    @Override
    public String toString() {
        return "ItemPrice{" +
                "name='" + this.name + '\'' +
                ", dateOptional=" + this.dateOptional +
                ", count=" + this.count +
                ", price='" + this.price + '\'' +
                '}';
    }
}
