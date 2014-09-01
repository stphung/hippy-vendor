package org.stphung;

import com.ep.hippyjava.bot.HippyBot;
import com.ep.hippyjava.model.Room;
import org.stphung.vending.Offer;
import org.stphung.vending.ShopEntry;
import org.stphung.vending.Vendor;
import org.stphung.vending.VendorListener;

public class VendingBot extends HippyBot implements VendorListener {
    public static final String ROOM_JID = "161862_stphung@conf.hipchat.com";
    public static final String USERNAME_JID = "161862_1163083@chat.hipchat.com";
    public static final String GROUP_API_KEY = "5e29cba45b3c1df3b48743ae8e90fe";

    private final Vendor vendor;
    private Room room;

    public VendingBot(Vendor vendor) {
        this.vendor = vendor;
    }

    @Override
    public String apiKey() {
        return GROUP_API_KEY;
    }

    private void say(String message) {
        if (this.room == null) {
            Room room = super.findRoom("Stphung");
            this.room = room;
        }
        this.sendMessage(message, this.room);
    }

    @Override
    public void receiveMessage(String message, String user, Room room) {
        try {
            if (user.equals("Steven Phung")) {
                if (message.equals("start")) {
                    this.say("starting vendor...");
                    this.vendor.start();
                } else if (message.startsWith("accept")) {
                    String[] tokens = message.split(" ");
                    String offerId = tokens[1];
                    this.vendor.confirmOffer(offerId);
                    this.say("Confirmed to use offer " + offerId);
                } else if (message.equals("stop")) {
                    this.say("stopping vendor...");
                }
            }
        } catch (Exception e) {
            System.out.println("failure " + e.getMessage());
        }
    }

    @Override
    public void onLoad() {
        this.joinRoom(ROOM_JID);
        System.out.println("Joined " + ROOM_JID);
    }

    @Override
    public String username() {
        return USERNAME_JID;
    }

    @Override
    public String nickname() {
        return "ro bot";
    }

    @Override
    public String password() {
        return "112585";
    }

    @Override
    public void offerCreated(Offer offer) {
        this.say("An offer was created (" + offer.getId() + ")");
        for (ShopEntry shopEntry : offer.getShopEntries()) {
            this.say(shopEntry.toString());
        }
    }
}
