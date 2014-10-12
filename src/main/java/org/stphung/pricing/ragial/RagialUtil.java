package org.stphung.pricing.ragial;

import com.google.common.collect.ImmutableList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.stphung.pricing.AverageTable;
import org.stphung.pricing.VendHistoryRecord;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public final class RagialUtil {
    public static AverageTable getShortAverageTable(String url) throws IOException {
        return getAverageTable(url, 0);
    }

    public static AverageTable getLongAverageTable(String url) throws IOException {
        return getAverageTable(url, 1);
    }

    private static AverageTable getAverageTable(String url, int tableIndex) throws IOException {
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

    public static List<VendHistoryRecord> getVendHistoryRecords(String url) throws IOException {
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
}
