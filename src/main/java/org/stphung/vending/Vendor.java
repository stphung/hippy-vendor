package org.stphung.vending;

import com.google.common.collect.ImmutableList;
import org.stphung.openkore.Openkore;
import org.stphung.openkore.OpenkoreException;
import org.stphung.pricing.ItemDataProvider;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

public class Vendor implements Closeable {
    private final Openkore openkore;
    private final List<VendorListener> vendorListeners;
    private final Map<String, Offer> offers;

    public Vendor(String openkoreHome) {
        this.openkore = new Openkore(openkoreHome);
        this.vendorListeners = new ArrayList<>();
        this.offers = new HashMap<>();
    }

    public void addVendorListener(VendorListener listener) {
        this.vendorListeners.add(listener);
    }

    public List<CartItem> getCartItems() throws OpenkoreException, FileNotFoundException, InterruptedException {
        this.openkore.start();
        Thread.sleep(25000);
        this.openkore.close();

        Scanner sc = new Scanner(new File(openkore.getConsoleLogPath()));
        List<CartItem> latestCartItems = null;
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.contains("---------------------- Cart ----------------------")) {
                latestCartItems = getCartItems(sc);
            }
        }

        return latestCartItems;
    }

    private List<CartItem> getCartItems(Scanner sc) throws FileNotFoundException {
        ImmutableList.Builder<CartItem> cartItemsBuilder = ImmutableList.builder();

        sc.nextLine();

        // parse cart items
        String line = sc.nextLine();
        while (!line.isEmpty()) {
            String[] tokens = line.split(" ");
            int count = Integer.parseInt(tokens[tokens.length - 1]);
            StringBuilder nameSb = new StringBuilder();
            for (int i = 1; i < tokens.length - 2; i++) {
                nameSb.append(tokens[i]).append(' ');
            }

            String name = nameSb.toString().trim();
            CartItem cartItem = new CartItem(name, count);
            cartItemsBuilder.add(cartItem);

            line = sc.nextLine();
        }

        ImmutableList<CartItem> cartItems = cartItemsBuilder.build();
        return cartItems;
    }

    public void createOffer(Planner planner, List<CartItem> cartItems, ItemDataProvider itemDataProvider) throws IOException, URISyntaxException {
        Offer offer = planner.getOffer(cartItems, itemDataProvider);
        this.offers.put(offer.getId(), offer);
        for (VendorListener vendorListener : this.vendorListeners) {
            vendorListener.offerCreated(offer);
        }
    }

    public void confirmOffer(String id) throws FileNotFoundException {
        Offer offer = this.offers.get(id);
        if (offer != null) {
            this.setOffer(offer);
        }
    }

    private void setOffer(Offer offer) throws FileNotFoundException {
        String shopConfigPath = this.openkore.getShopConfigPath();
        System.out.println("Writing openkore shop config (" + shopConfigPath + ")");
        try (PrintWriter pw = new PrintWriter(shopConfigPath)) {
            pw.println("Randoms");
            pw.println();
            for (ShopEntry item : offer.getShopEntries()) {
                pw.println(item.getName() + '\t' + item.getPrice() + '\t' + item.getCount());
            }
        }
        System.out.println("Wrote openkore shop config (" + shopConfigPath + ")");
    }

    public void start() throws OpenkoreException {
        this.openkore.start();
    }

    @Override
    public void close() throws IOException {
        this.openkore.close();
    }
}
