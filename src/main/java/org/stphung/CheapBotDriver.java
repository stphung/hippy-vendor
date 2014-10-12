package org.stphung;

import com.ep.hippyjava.HippyJava;
import com.ep.hippyjava.bot.HippyBot;
import org.stphung.openkore.OpenkoreException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CheapBotDriver {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        HippyBot bot = new CheapBot();
        EXECUTOR_SERVICE.execute(() -> HippyJava.runBot(bot));
    }
}
