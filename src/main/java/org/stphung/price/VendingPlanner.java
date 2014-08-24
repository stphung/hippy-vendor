package org.stphung;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.stphung.cart.CartItem;
import org.stphung.cart.VendibleCartItem;
import org.stphung.price.ItemPrice;
import org.stphung.price.ItemPriceProvider;
import org.stphung.price.RagialItemPriceProvider;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class VendingPlanner {
    public static VendingPlanner INSTANCE = new VendingPlanner();

    private static final int MIN_LEVENSHTEIN_DISTANCE = 0;
    private static final int MAXIMUM_PRICE_AGE = 60;

    public static VendingPlanner getInstance() {
        return INSTANCE;
    }

    private VendingPlanner() {
    }

    private static int dateDescending(ItemPrice p1, ItemPrice p2) {
        if (p1.getDateOptional().isPresent() && p2.getDateOptional().isPresent()) {
            return p2.getDateOptional().get().compareTo(p1.getDateOptional().get());
        } else if (!p1.getDateOptional().isPresent() && !p2.getDateOptional().isPresent()) {
            return 0;
        } else if (!p1.getDateOptional().isPresent()) {
            return -1;
        } else {
            return 1;
        }
    }

    public List<VendibleCartItem> getVendibleCartItems(Collection<CartItem> cartItems, ItemPriceProvider itemPriceProvider) throws IOException {
        ImmutableList.Builder<VendibleCartItem> builder = ImmutableList.builder();

        // TODO: parallelize this
        for (CartItem item : cartItems) {
            System.out.println("Retrieving item prices for " + item.getName());

            // get item prices
            List<ItemPrice> itemPrices = itemPriceProvider.getItemPrice(item.getName());

            // find closest match
            int minLevenshteinDistance = itemPrices.stream().mapToInt(i -> StringUtils.getLevenshteinDistance(item.getName(), i.getName())).min().getAsInt();

            // skip anything where we can't find a decent match
            if (minLevenshteinDistance > MIN_LEVENSHTEIN_DISTANCE) {
                continue;
            }

            List<ItemPrice> matches = itemPrices.stream().filter(i -> minLevenshteinDistance == StringUtils.getLevenshteinDistance(item.getName(), i.getName())).collect(Collectors.toList());

            System.out.println("Found prices: " + matches.stream().map(i -> i.getPrice()).collect(Collectors.toList()));

            // filter out prices older than a certain date
            LocalDateTime now = LocalDateTime.now();
            List<ItemPrice> sortedItemPrices = matches.stream()
                    .sorted(VendingPlanner::dateDescending)
                    .filter(i -> {
                        if (i.getDateOptional().isPresent()) {
                            LocalDateTime itemLdt = LocalDateTime.ofInstant(i.getDateOptional().get().toInstant(), ZoneId.systemDefault());
                            long days = Duration.between(itemLdt, now).toDays();
                            return days < MAXIMUM_PRICE_AGE;
                        } else {
                            return true;
                        }
                    })
                    .collect(Collectors.toList());

            // get the minimum price
            List<ItemPrice> currentlyVendingItems = sortedItemPrices.stream().filter(i -> i.getCount() > 0).collect(Collectors.toList());
            long targetPrice;
            if (currentlyVendingItems.size() > 0) {
                targetPrice = currentlyVendingItems.stream().mapToLong(ItemPrice::getPrice).min().getAsLong();
            } else {
                targetPrice = sortedItemPrices.stream().mapToLong(ItemPrice::getPrice).min().getAsLong();
            }

            VendibleCartItem vendibleCartItem = new VendibleCartItem(item.getName(), item.getCount(), targetPrice);
            builder.add(vendibleCartItem);
        }

        // sort descending, limit to 12 which is the maximum vend amount
        return builder.build().stream().sorted((a, b) -> descending(a, b)).limit(12).collect(Collectors.toList());
    }

    private int ascending(VendibleCartItem a, VendibleCartItem b) {
        return Long.compare(a.getPrice(), b.getPrice());
    }

    private int descending(VendibleCartItem a, VendibleCartItem b) {
        return Long.compare(b.getPrice(), a.getPrice());
    }
}
