package org.stphung;

import com.ep.hippyjava.HippyJava;
import org.stphung.openkore.OpenkoreException;
import org.stphung.vending.Vendor;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// TODO: relog using already approved shop.txt
// TODO: multi-user vending
// TODO: parameterize openkore

/**
 * must be run as admin
 * <p/>
 * openkore configuration:
 * - timeouts.txt - shop_useSkill_delay 5, openshop delay 30
 * - config.txt - shopAuto_open 1
 * - macros.txt
 * - macro plugin
 */
public class Main {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    public static void main(String[] args) throws IOException, XPathExpressionException, ParserConfigurationException, SAXException, InterruptedException, URISyntaxException, OpenkoreException {
        String openkoreHome = "C:/apps/openkore_ready";

        // create vendor and add listeners to it
        Vendor vendor = new Vendor(openkoreHome);
        VendingBot bot = new VendingBot(vendor);
        vendor.addVendorListener(bot);

        EXECUTOR_SERVICE.execute(() -> HippyJava.runBot(bot));
    }
}
