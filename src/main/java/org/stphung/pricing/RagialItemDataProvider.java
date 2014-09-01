package org.stphung.pricing;

import com.google.common.collect.ImmutableList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Optional;

// TODO: Use kimono
public class RagialItemDataProvider implements ItemDataProvider {
    public static final String RAGIAL_ENDPOINT = "http://ragial.com";
    private static final ItemDataProvider INSTANCE = new RagialItemDataProvider();

    private RagialItemDataProvider() {
    }

    public static ItemDataProvider getInstance() {
        return INSTANCE;
    }

    @Override
    public List<ItemData> getItemData(String itemName) throws IOException, URISyntaxException {
        String encodedItemName = URLEncoder.encode(itemName.toLowerCase(), "utf-8"); // TODO: why do I need to lowercase this for ragial to find it?
        ImmutableList.Builder<ItemData> builder = ImmutableList.builder();

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
                    // This means they are buying, the value is "B"
                }
            }

            long price = Long.parseLong(element.getElementsByClass("price").select("a").first().html().replaceAll(",", "").replaceAll("z", "").trim());
            builder.add(new ItemData(name, count, price, dateOpt));
        }

        return builder.build();
    }
}
