package org.stphung;

import com.ep.hippyjava.bot.HippyBot;
import com.ep.hippyjava.model.Room;
import com.google.common.collect.ImmutableMap;
import org.stphung.openkore.OpenkoreException;
import org.stphung.openkore.OpenkoreListener;
import org.stphung.vending.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;

// TODO: multithreading - don't block the command loop, i should be able to init two at the same time
// TODO: implement a feature to estimate our networth
public class VendingBot extends HippyBot implements VendorListener, OpenkoreListener, FileWatcherListener {
    public static final String ROOM_JID = "161862_stphung@conf.hipchat.com";
    public static final String USERNAME_JID = "161862_1163083@chat.hipchat.com";
    public static final String GROUP_API_KEY = "5e29cba45b3c1df3b48743ae8e90fe";
    public static final String NICKNAME = "ro bot";
    public static final String PASSWORD = "112585";
    private static final Logger LOGGER = Logger.getLogger(VendingBot.class.getCanonicalName());
    private final List<Vendor> vendors;
    private FileWatcher fileWatcher;
    private final ImmutableMap<Predicate<String>, BiConsumer<String, String>> handlers;
    private Room room;

    public VendingBot(List<Vendor> vendors) {
        this.vendors = vendors;
        this.handlers = this.createHandlers();
        this.vendors.forEach(v -> {
            v.addVendorListener(this);
            v.getOpenkore().addListener(this);
        });


        for (Vendor vendor : this.vendors) {
            Optional<String> shopLogPathOptional = vendor.getOpenkore().getShopLogPath();
            if (shopLogPathOptional.isPresent()) {
                LOGGER.info("found shop log, adding a file watcher");
                String shopLogPath = shopLogPathOptional.get();
                try {
                    this.fileWatcher = new FileWatcher(new File(shopLogPath));
                    this.fileWatcher.addListener(this);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                LOGGER.warning("shop log not found");
            }
        }
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

    private void botSay(String id, String message) {
        this.say(id + ": " + message);
    }

    private void ifVendorPresent(String m, Consumer<Vendor> consumer) {
        String[] tokens = m.split(" ");
        String vendorId = tokens[0];
        Optional<Vendor> vendorOptional = this.vendors.stream().filter(v -> v.getId().equals(vendorId)).findFirst();
        if (vendorOptional.isPresent()) {
            Vendor vendor = vendorOptional.get();
            consumer.accept(vendor);
        } else {
            LOGGER.warning("vendor " + vendorId + " not found");
        }
    }

    private boolean command(String m, String command) {
        try {
            return m.split(" ")[1].equals(command);
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    private ImmutableMap<Predicate<String>, BiConsumer<String, String>> createHandlers() {
        ImmutableMap.Builder<Predicate<String>, BiConsumer<String, String>> builder = ImmutableMap.builder();

        // init
        builder.put(s -> command(s, "init"), (m, u) -> {
            this.ifVendorPresent(m, vendor -> {
                try {
                    this.botSay(vendor.getId(), "initializing vendor");
                    vendor.init();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (OpenkoreException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            });
        });

        // start
        builder.put(s -> command(s, "start"), (m, u) -> {
            this.ifVendorPresent(m, vendor -> {
                try {
                    this.botSay(vendor.getId(), "starting vendor");
                    vendor.start();
                } catch (OpenkoreException e) {
                    e.printStackTrace();
                }
            });
        });

        // stop
        builder.put(s -> command(s, "stop"), (m, u) -> {
            this.ifVendorPresent(m, vendor -> {
                try {
                    this.botSay(vendor.getId(), "stopping vendor");
                    vendor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });

        // accept-offer
        builder.put(s -> command(s, "accept-offer"), (m, u) -> {
            this.ifVendorPresent(m, vendor -> {
                String offerId = m.split(" ")[2];
                try {
                    this.botSay(vendor.getId(), "accepting offer " + offerId);
                    vendor.confirmOffer(offerId);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });
        });

        // describe-offer
        builder.put(s -> command(s, "describe-offer"), (m, u) -> {
            String[] tokens = m.split(" ");
            String offerId = tokens[2];

            this.ifVendorPresent(m, vendor -> {
                Optional<Offer> offerOptional = vendor.getOffer(offerId);
                if (offerOptional.isPresent()) {
                    Offer offer = offerOptional.get();
                    StringBuilder sb = new StringBuilder("describing offer ").append(offer.getId()).append('\n');
                    List<ShopEntry> shopEntries = offer.getShopEntries();
                    for (int i = 0; i < shopEntries.size(); i++) {
                        sb.append(i).append(" - ").append(shopEntries.get(i)).append('\n');
                    }
                    this.botSay(vendor.getId(), sb.toString());
                }
            });
        });

        // print-offer
        builder.put(s -> command(s, "print-shop"), (m, u) -> {
            this.ifVendorPresent(m, vendor -> {
                try {
                    this.say(vendor.getShopConfig());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });

        return builder.build();
    }

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
    public void offerCreated(String id, Offer offer) {
        this.botSay(id, offer.getId() + " created");
        //this.say("describe-offer " + offer.getId());
    }

    @Override
    public void starting() {
        this.say("openkore starting");
    }

    @Override
    public void closing() {
        this.say("openkore closing");
    }

    @Override
    public void fileChanged(List<String> newLines) {
        // TODO: use hipchat notifications, associate with vendor id
        newLines.forEach(line -> this.say(line));
    }
}
