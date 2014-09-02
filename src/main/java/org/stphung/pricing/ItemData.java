package org.stphung.pricing;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ItemData {
    private final String name;
    private final int count;
    private final long price;
    private final Optional<Date> dateOptional;
    private final AverageTable shortAverageTable;
    private final AverageTable longAverageTable;
    private final List<VendHistoryRecord> vendHistoryRecords;

    public ItemData(String name, int count, long price, Optional<Date> dateOptional, AverageTable shortAverageTable, AverageTable longAverageTable, List<VendHistoryRecord> vendHistoryRecords) {
        this.name = name;
        this.dateOptional = dateOptional;
        this.count = count;
        this.price = price;
        this.shortAverageTable = shortAverageTable;
        this.longAverageTable = longAverageTable;
        this.vendHistoryRecords = vendHistoryRecords;
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

    public AverageTable getShortAverageTable() {
        return shortAverageTable;
    }

    public AverageTable getLongAverageTable() {
        return longAverageTable;
    }

    public List<VendHistoryRecord> getVendHistoryRecords() {
        return vendHistoryRecords;
    }

    @Override
    public String toString() {
        return "ItemData{" +
                "name='" + name + '\'' +
                ", count=" + count +
                ", price=" + price +
                ", dateOptional=" + dateOptional +
                ", shortAverageTable=" + shortAverageTable +
                ", longAverageTable=" + longAverageTable +
                ", vendHistoryRecords=" + vendHistoryRecords +
                '}';
    }
}
