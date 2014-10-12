package org.stphung.vending;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.stphung.pricing.ItemData;
import org.stphung.pricing.ItemDataProvider;
import org.stphung.pricing.VendHistoryRecord;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class Planner {
    private static final Logger LOGGER = Logger.getLogger(Planner.class.getCanonicalName());
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
        Map<String, List<ItemData>> itemDataMap = Maps.newHashMap();

        for (CartItem item : cartItems) {
            try {
                LOGGER.info("Retrieving item prices for " + item.getName());

                // get all item prices from ragial
                List<ItemData> itemDataList = itemDataMap.get(item.getName());
                if (itemDataList == null) {
                    LOGGER.info(item.getName() + " is not cached");
                    itemDataList = itemDataProvider.getItemData(item.getName());
                    itemDataMap.put(item.getName(), itemDataList);
                } else {
                    LOGGER.info(item.getName() + " is cached");
                }

                // find closest match and skip if we did not find one close enough
                int minLevenshteinDistance = itemDataList.stream().mapToInt(i -> StringUtils.getLevenshteinDistance(item.getName(), i.getName())).min().getAsInt();
                if (minLevenshteinDistance > MIN_LEVENSHTEIN_DISTANCE) {
                    continue;
                }

                // get the item data
                Optional<ItemData> itemDataOptional = itemDataList.stream()
                        .filter(i -> minLevenshteinDistance == StringUtils.getLevenshteinDistance(item.getName(), i.getName())) // only use those which match closely enough
                        .sorted(Planner::dateDescending) // sort according to time
                        .filter(i -> { // filter out old data
                            if (i.getDateOptional().isPresent()) {
                                LocalDateTime itemLdt = LocalDateTime.ofInstant(i.getDateOptional().get().toInstant(), ZoneId.systemDefault());
                                long days = Duration.between(itemLdt, LocalDateTime.now()).toDays();
                                return days < MAXIMUM_PRICE_AGE;
                            } else {
                                return true;
                            }
                        })
                        .findFirst();

                if (itemDataOptional.isPresent()) {
                    ItemData itemData = itemDataOptional.get();
                    Optional<Date> dateOptional = itemData.getDateOptional();

                    if (dateOptional.isPresent()) {
                        // it is not vending
                        int maxShortPrice = itemData.getShortAverageTable().getMax();
                        ShopEntry shopEntry = new ShopEntry(item.getName(), item.getCount(), maxShortPrice);
                        builder.add(shopEntry);
                    } else {
                        // it is vending
                        List<VendHistoryRecord> vendingVendHistoryRecords = itemData.getVendHistoryRecords().stream().filter(record -> !record.getDateOptional().isPresent()).collect(Collectors.toList());

                        // TODO: there could be no value present here if there are no vend history records
                        int min = vendingVendHistoryRecords.stream().mapToInt(VendHistoryRecord::getPrice).min().getAsInt();
                        int average = itemData.getShortAverageTable().getAverage();
                        double discountPercentage = (1.0 * (average - min)) / average;
                        LOGGER.info("discount on " + item.getName() + " is " + discountPercentage);

                        // only sell the items that aren't heavily discounted and sell them at max price
                        if (discountPercentage < 0.10) {
                            int max = itemData.getShortAverageTable().getMax();
                            ShopEntry shopEntry = new ShopEntry(item.getName(), item.getCount(), max);
                            builder.add(shopEntry);
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.warning("Failed to process cart item " + item + " " + e.getMessage());
                continue;
            }
        }

        // sort descending, limit to 12 which is the maximum vend amount
        List<ShopEntry> priceSortedShopEntries = builder.build().stream().sorted((a, b) -> descending(a, b)).limit(12).collect(Collectors.toList());
        return new Offer(UUID.randomUUID().toString(), priceSortedShopEntries);
    }

    private int descending(ShopEntry a, ShopEntry b) {
        return Long.compare(b.getPrice(), a.getPrice());
    }
}
