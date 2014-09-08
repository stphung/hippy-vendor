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

public class RagialItemDataProvider implements ItemDataProvider {
    public static final String RAGIAL_ENDPOINT = "http://ragial.com";
    private static final ItemDataProvider INSTANCE = new RagialItemDataProvider();

    private RagialItemDataProvider() {
    }

    public static ItemDataProvider getInstance() {
        return INSTANCE;
    }

    private AverageTable getAverageTable(String url, int tableIndex) throws IOException {
        Document document = Jsoup.connect(url).get();
        Element avgtable = document.getElementById("avgtable");
        Element stats = avgtable.select("table tbody tr").get(tableIndex);

        Elements rows = stats.select("tr").select("td");
        int count = Integer.parseInt(rows.get(0).html());
        int min = Integer.parseInt(rows.get(1).html().replaceAll(",", "").split("z")[0]);
        int max = Integer.parseInt(rows.get(2).html().replaceAll(",", "").split("z")[0]);
        int average = Integer.parseInt(rows.get(3).html().replaceAll(",", "").split("z")[0]);
        int standardDeviation = Integer.parseInt(rows.get(4).html().replaceAll(",", "").split(";")[1].split("z")[0]);
        double confidence = Double.parseDouble(rows.get(5).html());

        return new AverageTable(count, min, max, average, standardDeviation, confidence);
    }

    private List<VendHistoryRecord> getVendHistoryRecords(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        Element avgtable = document.getElementById("selltable");
        Elements rows = avgtable.select("table tbody tr");

        ImmutableList.Builder<VendHistoryRecord> builder = ImmutableList.builder();
        for (Element row : rows) {
            Elements td = row.select("td");
            String date = td.get(0).select("a").first().html();
            int count = Integer.parseInt(td.get(1).html().replaceAll(",", "").replaceAll("x", ""));
            int price = Integer.parseInt(td.get(2).select("a").html().replaceAll(",", "").replaceAll("z", ""));
            Optional<String> dateOptional = date.equals("Vending Now") ? Optional.empty() : Optional.of(date);
            VendHistoryRecord vendHistoryRecord = new VendHistoryRecord(dateOptional, count, price);
            builder.add(vendHistoryRecord);
        }

        return builder.build();
    }

    @Override
    public List<ItemData> getItemData(String itemName) throws IOException, URISyntaxException {
        String encodedItemName = URLEncoder.encode(itemName.toLowerCase(), "utf-8"); // TODO: why do I need to lowercase this for ragial to find it?
        ImmutableList.Builder<ItemData> builder = ImmutableList.builder();

        Document doc = Jsoup.connect(RAGIAL_ENDPOINT + "/search/iRO-Renewal/" + encodedItemName).get();

        Elements trElements = doc.getElementsByClass("ilist").select("tbody tr");
        for (int i = 0; i < trElements.size(); i++) {
            Element element = trElements.get(i);
            String itemUrl = element.getElementsByClass("name").select("a").attr("href");

            AverageTable shortAverageTable = getAverageTable(itemUrl, 0);
            AverageTable longAverageTable = getAverageTable(itemUrl, 1);
            List<VendHistoryRecord> vendHistoryRecords = getVendHistoryRecords(itemUrl);

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
            builder.add(new ItemData(name, count, price, dateOpt, shortAverageTable, longAverageTable, vendHistoryRecords));
        }

        return builder.build();
    }
}
