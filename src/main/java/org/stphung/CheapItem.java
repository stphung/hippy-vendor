package org.stphung;

/**
 * Created by phungs on 9/26/14.
 */
public class CheapItem {
    private final String name;
    private final int count;
    private final int price;
    private final int discount;

    public CheapItem(String name, int count, int price, int discount) {
        this.name = name;
        this.count = count;
        this.price = price;
        this.discount = discount;
    }

    public String getName() {
        return this.name;
    }

    public int getCount() {
        return this.count;
    }

    public int getPrice() {
        return this.price;
    }

    public int getDiscount() {
        return this.discount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CheapItem cheapItem = (CheapItem) o;

        if (count != cheapItem.count) return false;
        if (discount != cheapItem.discount) return false;
        if (price != cheapItem.price) return false;
        if (!name.equals(cheapItem.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + count;
        result = 31 * result + price;
        result = 31 * result + discount;
        return result;
    }

    @Override
    public String toString() {
        return "CheapItem{" +
                "name='" + this.name + '\'' +
                ", count=" + this.count +
                ", price=" + this.price +
                ", discount=" + this.discount +
                '}';
    }
}
