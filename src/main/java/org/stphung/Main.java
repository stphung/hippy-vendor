package org.stphung;

import com.ep.hippyjava.HippyJava;
import com.google.common.collect.ImmutableList;
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
        VendingBot bot = new VendingBot(ImmutableList.of(
                new Vendor("C:/apps/openkore_ready", "Randoms", "bot1"),
                new Vendor("C:/apps/openkore_ready_2", "Corner Shop", "bot2")
        ));

        EXECUTOR_SERVICE.execute(() -> HippyJava.runBot(bot));
    }
}
