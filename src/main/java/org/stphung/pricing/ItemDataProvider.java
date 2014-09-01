package org.stphung.pricing;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface ItemDataProvider {
    List<ItemData> getItemData(String itemName) throws IOException, URISyntaxException;
}
