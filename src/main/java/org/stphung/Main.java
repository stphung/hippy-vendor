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

// TODO: relog using already approved shop.txt
// TODO: multi-user vending
// TODO: consider using ragial to look for vend data
/**
 * this must be run as admin
 *
 * openkore configuration:
 *   - timeouts.txt - shop_useSkill_delay
 *   - config.txt - shopAuto_open 1
 *   - macros.txt and macro plugin
 */
public class Main {
    private static final VendingPlanner VENDING_PLANNER = VendingPlanner.getInstance();

    public static void main(String[] args) throws IOException, XPathExpressionException, ParserConfigurationException, SAXException, InterruptedException {
        // create an openkore
        String openkoreHome = "C:/apps/openkore_ready";
        Openkore openkore = new Openkore(openkoreHome);

        // get latest cart items
        Scanner sc = new Scanner(new File(openkore.getConsoleLogPath()));
        List<CartItem> latestCartItems = null;
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.contains("---------------------- Cart ----------------------")) {
                latestCartItems = getCartItems(sc);
            }
        }

        // determine what to vend
        List<VendibleCartItem> vendibleCartItems = VENDING_PLANNER.getVendibleCartItems(latestCartItems, RagialItemPriceProvider.getInstance());

        // confirm the vending proposal
        Scanner prompt = new Scanner(System.in);
        confirmVendingProposal(prompt, vendibleCartItems);

        // write the vending proposal
        writeShopConfig(vendibleCartItems, openkore.getShopConfigPath());

        // allow manual modification and tweaking
        System.out.print("Press enter when ready to vend:");
        prompt.nextLine();
        openkore.start();
    }

    private static void confirmVendingProposal(Scanner sc, List<VendibleCartItem> vendibleCartItems) {
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
    }

    private static void writeShopConfig(List<VendibleCartItem> vendibleCartItems, String shopConfigPath) throws FileNotFoundException {
        System.out.println("Writing openkore shop config (" + shopConfigPath + ")");
        try (PrintWriter pw = new PrintWriter(shopConfigPath)) {
            pw.println("Randoms");
            pw.println();
            for (VendibleCartItem item : vendibleCartItems) {
                pw.println(item.getName() + '\t' + item.getPrice() + '\t' + item.getCount());
            }
        }

        System.out.println("Wrote openkore shop config (" + shopConfigPath + ")");
    }

    private static List<CartItem> getCartItems(Scanner sc) throws FileNotFoundException {
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
}
