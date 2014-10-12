package org.stphung.pricing;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * An abstraction for retrieving item data given a query.
 */
public interface ItemDataProvider {
    List<ItemData> getItemData(String query) throws IOException, URISyntaxException;
}
