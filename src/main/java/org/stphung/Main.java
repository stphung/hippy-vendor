package org.stphung;

import com.google.common.collect.ImmutableList;
import org.stphung.cart.CartItem;
import org.stphung.cart.VendibleCartItem;
import org.stphung.price.RagialItemPriceProvider;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * this must be run as admin
 *
 * openkore configuration:
 *   - timeouts.txt - shop_useSkill_delay
 *   - config.txt - shopAuto_open 1
 */
public class Main {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    private static final String OPENKORE_HOME = "C:/apps/openkore_ready";
    private static final String ACCOUNT_NAME = "3xtz_l";
    private static final int CHARACTER_INDEX = 0;

    public static void main(String[] args) throws IOException, XPathExpressionException, ParserConfigurationException, SAXException, InterruptedException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        URL cartUrl = classloader.getResource("cart.txt");
        List<CartItem> cartItems = getCartItems(cartUrl.getPath());

        Scanner sc = new Scanner(System.in);

        // 0. get the latest cart data
        // TODO: automate getting the cart data somehow?
        /**
         * maybe we can login
         * wait for awhile
         * send to the outputstream "cart"
         * exit
         * parse the most recent cart output block
         */

        // 1. determine what to vend
        VendingPlanner vendingPlanner = VendingPlanner.getInstance();
        List<VendibleCartItem> vendibleCartItems = vendingPlanner.getVendibleCartItems(cartItems, RagialItemPriceProvider.getInstance());

        // 2. confirm the vending proposal
        System.out.println("Vending Proposal");
        System.out.println("----------------------------------------------------------------------------------------");
        for (VendibleCartItem vendibleCartItem : vendibleCartItems) {
            System.out.println(vendibleCartItem);
        }
        System.out.println("----------------------------------------------------------------------------------------");
        System.out.print("Type y if this is ok: ");
        String response = sc.nextLine();
        if (!response.equals("y")) {
            System.exit(0);
        }

        // 3. write the vending proposal
        String shopConfigPath = OPENKORE_HOME + "/control/shop.txt";
        System.out.println("Writing openkore shop config (" + shopConfigPath + ")");
        try (PrintWriter pw = new PrintWriter(shopConfigPath)) {
            pw.println("Randoms");
            pw.println();
            for (VendibleCartItem item : vendibleCartItems) {
                pw.println(item.getName() + '\t' + item.getPrice() + '\t' + item.getCount());
            }
        }

        System.out.println("Wrote openkore shop config (" + shopConfigPath + ")");

        // 4. allow manual modification and tweaking
        System.out.print("Press enter when ready to vend:");
        sc.nextLine();

        // 5. start openkore
        Process process = getOpenkoreRunnable(OPENKORE_HOME);

        EXECUTOR_SERVICE.submit(() -> {
            Scanner sc1 = new Scanner(process.getErrorStream());

            // TODO: not sure why this for loop has to be here, but using waitFor does not work.
            while (sc1.hasNextLine()) {
                sc1.nextLine();
            }
        });

        EXECUTOR_SERVICE.awaitTermination(300, TimeUnit.DAYS);

        // TODO: relog using already approved shop.txt
        // TODO: multi-user vending
        // TODO: consider using ragial to look for vend data
    }

    private static Process getOpenkoreRunnable(String openkoreHome) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(openkoreHome + "/start.exe");
        processBuilder.directory(new File(openkoreHome));
        Process process = null;
        try {
            process = processBuilder.start();
            return process;
        } catch (IOException e) {
            throw e;
        }
    }

    private static List<CartItem> getCartItems(String filePath) throws FileNotFoundException {
        ImmutableList.Builder<CartItem> cartItemsBuilder = ImmutableList.builder();
        try (Scanner sc = new Scanner(new File(filePath))) {
            // skip header
            sc.nextLine();
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
        }

        ImmutableList<CartItem> cartItems = cartItemsBuilder.build();
        return cartItems;
    }
}
