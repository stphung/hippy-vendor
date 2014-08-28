package org.stphung.price;

import java.io.IOException;
import java.util.List;

public interface ItemPriceProvider {
    List<ItemPrice> getItemPrice(String itemName) throws IOException;
}
