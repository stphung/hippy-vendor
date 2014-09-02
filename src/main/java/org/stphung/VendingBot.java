package org.stphung;

import com.ep.hippyjava.bot.HippyBot;
import com.ep.hippyjava.model.Room;
import com.google.common.collect.ImmutableMap;
import org.stphung.openkore.OpenkoreException;
import org.stphung.openkore.OpenkoreListener;
import org.stphung.vending.Offer;
import org.stphung.vending.ShopEntry;
import org.stphung.vending.Vendor;
import org.stphung.vending.VendorListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class VendingBot extends HippyBot implements VendorListener, OpenkoreListener {
    public static final String ROOM_JID = "161862_stphung@conf.hipchat.com";
    public static final String USERNAME_JID = "161862_1163083@chat.hipchat.com";
    public static final String GROUP_API_KEY = "5e29cba45b3c1df3b48743ae8e90fe";
    public static final String NICKNAME = "ro bot";
    public static final String PASSWORD = "112585";
    private static final Logger LOGGER = Logger.getLogger(VendingBot.class.getCanonicalName());
    private final Vendor vendor;
    private final ImmutableMap<Predicate<String>, BiConsumer<String, String>> handlers;
    private Room room;

    public VendingBot(Vendor vendor) {
        this.vendor = vendor;
        this.handlers = this.createHandlers();
        this.vendor.getOpenkore().addListener(this);
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

    private ImmutableMap<Predicate<String>, BiConsumer<String, String>> createHandlers() {
        ImmutableMap.Builder<Predicate<String>, BiConsumer<String, String>> builder = ImmutableMap.builder();

        // init
        builder.put(s -> s.equals("init"), (m, u) -> {
            this.say("initializing vendor");
            try {
                this.vendor.init();
            } catch (OpenkoreException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // start
        builder.put(s -> s.equals("vend"), (m, u) -> {
            this.say("starting vendor");
            try {
                this.vendor.start();
            } catch (OpenkoreException e) {
                e.printStackTrace();
            }
        });

        // accept-offer
        builder.put(s -> s.startsWith("accept-offer"), (m, u) -> {
            String[] tokens = m.split(" ");
            String offerId = tokens[1];
            try {
                this.vendor.confirmOffer(offerId);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            this.say("accepting offer " + offerId);
        });

        // modify-offer <offer-id> <index> <limit|price> <value>
        builder.put(s -> s.startsWith("modify-offer"), (m, u) -> {
            String[] tokens = m.split(" ");
            String offerId = tokens[1];
            int index = Integer.parseInt(tokens[2]);
            String mode = tokens[3];
            int value = Integer.parseInt(tokens[4]);
            System.out.println("offerId = " + offerId);
            System.out.println("index = " + index);
            System.out.println("mode = " + mode);
            System.out.println("value = " + value);

            if (mode.equals("limit")) {
                vendor.modifyOfferItemLimit(offerId, index, value);
            } else if (mode.equals("price")) {
                vendor.modifyOfferItemPrice(offerId, index, value);
            }
        });

        // describe-offer
        builder.put(s -> s.startsWith("describe-offer"), (m, u) -> {
            String[] tokens = m.split(" ");
            String offerId = tokens[1];

            Optional<Offer> offerOptional = this.vendor.getOffer(offerId);
            if (offerOptional.isPresent()) {
                Offer offer = offerOptional.get();
                this.say("describing offer " + offer.getId());
                List<ShopEntry> shopEntries = offer.getShopEntries();
                for (int i = 0; i < shopEntries.size(); i++) {
                    this.say(i + " - " + shopEntries.get(i));
                }
            }
        });

        // print-offer
        builder.put(s -> s.equals("print-shop"), (m, u) -> {
            try {
                this.say(this.vendor.getShopConfig());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // TODO: list-offers

        // TODO: get state

        return builder.build();
    }

    // TODO: I want to know when things sell, send a message to hipchat

    @Override
    public void receiveMessage(String message, String user, Room room) {
        for (Map.Entry<Predicate<String>, BiConsumer<String, String>> entry : this.handlers.entrySet()) {
            if (entry.getKey().test(message)) {
                try {
                    entry.getValue().accept(message, user);
                } catch (Exception e) {
                    String msg = "error processing handler: " + e.getMessage();
                    LOGGER.warning(msg);
                    e.printStackTrace();
                    this.say(msg);
                }
                break;
            }
        }
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

    @Override
    public void offerCreated(Offer offer) {
        this.say("offer " + offer.getId() + " created");
        for (ShopEntry shopEntry : offer.getShopEntries()) {
            this.say(shopEntry.toString());
        }
    }

    @Override
    public void starting() {
        this.say("openkore starting");
    }

    @Override
    public void closing() {
        this.say("openkore closing");
    }
}
