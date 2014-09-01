package org.stphung;

import com.ep.hippyjava.HippyJava;
import org.stphung.openkore.OpenkoreException;
import org.stphung.pricing.RagialItemDataProvider;
import org.stphung.vending.CartItem;
import org.stphung.vending.Planner;
import org.stphung.vending.Vendor;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// TODO: relog using already approved shop.txt
// TODO: multi-user vending
// TODO: parameterize openkore

/**
 * must be run as admin
 *
 * openkore configuration:
 * - timeouts.txt - shop_useSkill_delay
 * - config.txt - shopAuto_open 1
 * - macros.txt
 * - macro plugin
 */
public class Main {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    private static final Planner VENDING_PLANNER = Planner.getInstance();

    public static void main(String[] args) throws IOException, XPathExpressionException, ParserConfigurationException, SAXException, InterruptedException, URISyntaxException, OpenkoreException {
        String openkoreHome = "C:/apps/openkore_ready";

        // create vendor and add listeners to it
        Vendor vendor = new Vendor(openkoreHome);
        VendingBot bot = new VendingBot(vendor);
        vendor.addVendorListener(bot);

        EXECUTOR_SERVICE.execute(() -> {
            HippyJava.runBot(bot);
        });

        Thread.sleep(10000);

        // create an offer
        List<CartItem> latestCartItems = vendor.getCartItems();
        vendor.createOffer(VENDING_PLANNER, latestCartItems, RagialItemDataProvider.getInstance());
    }
}
