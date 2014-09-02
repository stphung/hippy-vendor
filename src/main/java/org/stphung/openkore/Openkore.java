package org.stphung.openkore;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Openkore implements Closeable {
    private static final Logger LOGGER = Logger.getLogger(Openkore.class.getCanonicalName());

    private final ExecutorService executorService;
    private final String openkoreHome;
    private final List<OpenkoreListener> listeners;
    private Process process;

    public Openkore(String openkoreHome) {
        this.openkoreHome = openkoreHome;
        this.executorService = Executors.newCachedThreadPool();
        this.listeners = new ArrayList<>();
    }

    public void addListener(OpenkoreListener listener) {
        this.listeners.add(listener);
    }

    private Process getOpenkoreProcess(String openkoreHome) throws OpenkoreException {
        ProcessBuilder processBuilder = new ProcessBuilder(openkoreHome + "/start.exe");
        processBuilder.directory(new File(openkoreHome));
        Process process;
        try {
            process = processBuilder.start();
            return process;
        } catch (IOException e) {
            throw new OpenkoreException(e);
        }
    }

    public void start() throws OpenkoreException {
        if (this.process != null && this.process.isAlive()) {
            this.close();
        }

        for (OpenkoreListener listener : this.listeners) {
            listener.starting();
        }

        LOGGER.info("starting openkore process @ " + this.openkoreHome);
        Process openkoreProcess = getOpenkoreProcess(this.openkoreHome);
        this.process = openkoreProcess;
        this.executorService.submit(() -> {
            Scanner scanner = new Scanner(openkoreProcess.getErrorStream());
            // TODO: Not sure why this for loop has to be here, but using waitFor does not work.
            while (scanner.hasNextLine()) {
                scanner.nextLine();
            }
        });
    }

    public String getShopConfigPath() {
        String shopConfigPath = openkoreHome + "/control/shop.txt";
        return shopConfigPath;
    }

    public String getConsoleLogPath() {
        String consoleLogPath = openkoreHome + "/logs/console.txt";
        return consoleLogPath;
    }

    @Override
    public void close() {
        for (OpenkoreListener listener : this.listeners) {
            listener.closing();
        }

        LOGGER.info("terminating openkore process @ " + this.openkoreHome);
        this.process.destroy();
    }
}
