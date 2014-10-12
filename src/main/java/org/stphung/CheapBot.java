package org.stphung;

import com.ep.hippyjava.bot.HippyBot;
import com.ep.hippyjava.model.Room;
import com.google.common.collect.ImmutableList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;


public class CheapBot extends HippyBot {
    private static final String ROOM_JID = "161862_stphung@conf.hipchat.com";
    private static final String USERNAME_JID = "161862_1163083@chat.hipchat.com";
    private static final String GROUP_API_KEY = "5e29cba45b3c1df3b48743ae8e90fe";
    private static final String NICKNAME = "ro bot";
    private static final String PASSWORD = "112585";
    private static final Logger LOGGER = Logger.getLogger(VendingBot.class.getCanonicalName());

    private final ExecutorService executorService;
    private Room room;

    public CheapBot() {
        this.executorService = Executors.newCachedThreadPool();
        this.executorService.execute(() -> {
            Set<CheapItem> found = new HashSet<>();
            Random random = new Random();
            while (true) {
                try {
                    Thread.sleep(30000+random.nextInt(30000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    List<CheapItem> cheapItems = this.getCheapItems();
                    for (CheapItem item : cheapItems) {
                        if (item.getPrice() > 1000000 && !found.contains(item)) {
                            this.say(item.toString());
                            found.add(item);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private List<CheapItem> getCheapItems() throws IOException {
        List<String> urls = ImmutableList.of(
                "http://ragial.com/cheap/iRO-Renewal/0",
                "http://ragial.com/cheap/iRO-Renewal/2",
                "http://ragial.com/cheap/iRO-Renewal/3",
                "http://ragial.com/cheap/iRO-Renewal/4",
                "http://ragial.com/cheap/iRO-Renewal/5",
                "http://ragial.com/cheap/iRO-Renewal/5");

        ImmutableList.Builder<CheapItem> builder = ImmutableList.builder();
        for (String url : urls) {
            Document doc = Jsoup.connect(url).get();
            Elements tr = doc.select("tr");
            for (int i = 1; i < tr.size(); i++) {
                Element row = tr.get(i);

                // name
                Elements nameElements = row.getElementsByClass("name").select("a");
                String itemName = nameElements.html().split(">")[1].trim();
                //String shopUrl = nameElements.select("a").attr("href");

                // price
                Elements priceElements = row.getElementsByClass("price").select("a");
                //String itemUrl = priceElements.select("a").attr("href");
                int price = Integer.parseInt(priceElements.html().split("z")[0].replaceAll(",", ""));

                // count
                Elements amtElements = row.getElementsByClass("amt");
                int count = Integer.parseInt(amtElements.html().split("x")[0].replaceAll(",", ""));

                // discount
                Elements stdElements = row.getElementsByClass("std");
                int discount = Integer.parseInt(stdElements.html().split("%")[0].replaceAll(",", ""));

                CheapItem item = new CheapItem(itemName, count, price, discount);
                builder.add(item);
            }
        }

        ImmutableList<CheapItem> items = builder.build();
        return items;
    }

    @Override
    public String apiKey() {
        return GROUP_API_KEY;
    }

    private void say(String message) {
        LOGGER.info("saying: " + message);
        if (this.room == null) {
            Room room = super.findRoom("Stphung");
            this.room = room;
        }
        this.sendMessage(message, this.room);
    }


    @Override
    public void receiveMessage(String message, String user, Room room) {
    }

    @Override
    public void onLoad() {
        this.joinRoom(ROOM_JID);
        LOGGER.info("joined " + ROOM_JID);
    }

    @Override
    public String username() {
        return USERNAME_JID;
    }

    @Override
    public String nickname() {
        return NICKNAME;
    }

    @Override
    public String password() {
        return PASSWORD;
    }
}
