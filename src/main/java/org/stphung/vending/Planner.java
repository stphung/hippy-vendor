package org.stphung.vending;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.stphung.pricing.ItemData;
import org.stphung.pricing.ItemDataProvider;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class Planner {
    private static final int MIN_LEVENSHTEIN_DISTANCE = 0;
    private static final int MAXIMUM_PRICE_AGE = 60;
    public static Planner INSTANCE = new Planner();

    private Planner() {
    }

    public static Planner getInstance() {
        return INSTANCE;
    }

    private static int dateDescending(ItemData p1, ItemData p2) {
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

    public Offer getOffer(Collection<CartItem> cartItems, ItemDataProvider itemDataProvider) throws IOException, URISyntaxException {
        ImmutableList.Builder<ShopEntry> builder = ImmutableList.builder();

        for (CartItem item : cartItems) {
            System.out.println("Retrieving item prices for " + item.getName());

            // get item prices
            List<ItemData> itemDatas = itemDataProvider.getItemData(item.getName());

            // TODO: figure out what items are hot and which aren't, vend the items that are not hot

            // find closest match
            int minLevenshteinDistance = itemDatas.stream().mapToInt(i -> StringUtils.getLevenshteinDistance(item.getName(), i.getName())).min().getAsInt();

            // skip anything where we can't find a decent match
            if (minLevenshteinDistance > MIN_LEVENSHTEIN_DISTANCE) {
                continue;
            }

            List<ItemData> matches = itemDatas.stream().filter(i -> minLevenshteinDistance == StringUtils.getLevenshteinDistance(item.getName(), i.getName())).collect(Collectors.toList());

            System.out.println("Found relevant prices: " + matches.stream().map(i -> i.getPrice()).collect(Collectors.toList()));

            // filter out prices older than a certain date
            LocalDateTime now = LocalDateTime.now();
            List<ItemData> sortedItemDatas = matches.stream()
                    .sorted(Planner::dateDescending)
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

            // TODO: sell most expensive when count*price

            // get the minimum price
            List<ItemData> currentlyVendingItems = sortedItemDatas.stream().filter(i -> i.getCount() > 0).collect(Collectors.toList());
            long targetPrice;
            if (currentlyVendingItems.size() > 0) {
                targetPrice = currentlyVendingItems.stream().mapToLong(ItemData::getPrice).min().getAsLong();
            } else {
                try {
                    targetPrice = sortedItemDatas.stream().mapToLong(ItemData::getPrice).min().getAsLong();
                } catch (Exception e) {
                    System.out.println("failed to find a price");
                    continue;
                }
            }

            ShopEntry shopEntry = new ShopEntry(item.getName(), item.getCount(), targetPrice);
            builder.add(shopEntry);
        }

        // sort descending, limit to 12 which is the maximum vend amount
        List<ShopEntry> priceSortedShopEntries = builder.build().stream().sorted((a, b) -> descending(a, b)).limit(12).collect(Collectors.toList());
        return new Offer(UUID.randomUUID().toString(), priceSortedShopEntries);
    }

    private int ascending(ShopEntry a, ShopEntry b) {
        return Long.compare(a.getPrice(), b.getPrice());
    }

    private int descending(ShopEntry a, ShopEntry b) {
        return Long.compare(b.getPrice(), a.getPrice());
    }
}
