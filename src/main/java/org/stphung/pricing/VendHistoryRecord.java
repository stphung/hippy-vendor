package org.stphung.pricing;

import java.util.Date;
import java.util.Optional;

public class VendHistoryRecord {
    private final Optional<String> dateOptional;
    private final int count;
    private final int price;

    public VendHistoryRecord(Optional<String> dateOptional, int count, int price) {
        this.dateOptional = dateOptional;
        this.count = count;
        this.price = price;
    }

    public Optional<String> getDateOptional() {
        return dateOptional;
    }

    public int getCount() {
        return count;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "VendHistoryRecord{" +
                "dateOptional=" + dateOptional +
                ", count=" + count +
                ", price=" + price +
                '}';
    }
}
