package org.stphung;

import com.ep.hippyjava.HippyJava;
import com.google.common.collect.ImmutableList;
import org.stphung.openkore.OpenkoreException;
import org.stphung.vending.Vendor;

import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by phungs on 9/26/14.
 */
public class VendingBotDriver {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        VendingBot bot = new VendingBot(ImmutableList.of(
                new Vendor("C:/apps/openkore_ready", "Randoms", "bot1"),
                new Vendor("C:/apps/openkore_ready_2", "Corner Shop", "bot2"),
                new Vendor("C:/apps/openkore_ready_3", "Get Some!", "bot3"),
                new Vendor("C:/apps/openkore_ready_4", "La Shoppe", "bot4")
        ));

        EXECUTOR_SERVICE.execute(() -> HippyJava.runBot(bot));
    }
}
