package org.stphung.price;

import com.google.common.collect.ImmutableList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Optional;

// TODO: Use kimono
public class RagialItemPriceProvider implements ItemPriceProvider {
    private static final ItemPriceProvider INSTANCE = new RagialItemPriceProvider();
    public static final String RAGIAL_ENDPOINT = "http://ragial.com";

    public static ItemPriceProvider getInstance() {
        return INSTANCE;
    }

    private RagialItemPriceProvider() {
    }

    @Override
    public List<ItemPrice> getItemPrice(String itemName) throws IOException {
        String encodedItemName = URLEncoder.encode(itemName, "utf-8");
        ImmutableList.Builder<ItemPrice> builder = ImmutableList.builder();

        Document doc = Jsoup.connect(RAGIAL_ENDPOINT + "/search/iRO-Renewal/" + encodedItemName).get();

        Elements trElements = doc.getElementsByClass("ilist").select("tbody tr");
        for (int i = 0; i < trElements.size(); i++) {
            Element element = trElements.get(i);
            String name = element.getElementsByClass("name").select("a").first().html().split(">")[1].trim();

            Elements date = element.getElementsByClass("date");
            Optional<Date> dateOpt;
            if (date.size() > 0) {
                String dateString = date.first().html();
                dateOpt = Optional.of(new Date(dateString));
            } else {
                dateOpt = Optional.empty();
            }

            Elements cnt = element.getElementsByClass("cnt");
            int count = 0;
            if (cnt.size() > 0) {
                try {
                    count = Integer.parseInt(cnt.first().html());
                } catch (NumberFormatException e) {
                    // they are buying, the value is "B"
                }
            }

            long price = Long.parseLong(element.getElementsByClass("price").select("a").first().html().replaceAll(",", "").replaceAll("z", "").trim());
            builder.add(new ItemPrice(name, count, price, dateOpt));
        }

        return builder.build();
    }
}
