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
        return this.shortAverageTable;
    }

    public AverageTable getLongAverageTable() {
        return this.longAverageTable;
    }

    public List<VendHistoryRecord> getVendHistoryRecords() {
        return this.vendHistoryRecords;
    }

    @Override
    public String toString() {
        return "ItemData{" +
                "name='" + this.name + '\'' +
                ", count=" + this.count +
                ", price=" + this.price +
                ", dateOptional=" + this.dateOptional +
                ", shortAverageTable=" + this.shortAverageTable +
                ", longAverageTable=" + this.longAverageTable +
                ", vendHistoryRecords=" + this.vendHistoryRecords +
                '}';
    }
}
