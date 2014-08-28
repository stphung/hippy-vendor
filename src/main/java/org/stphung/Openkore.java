package org.stphung;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Openkore implements Closeable {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    private final String openkoreHome;

    public Openkore(String openkoreHome) {
        this.openkoreHome = openkoreHome;
    }

    private Process getOpenkoreProcess(String openkoreHome) throws IOException {
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

    public void start() throws IOException {
        Process openkoreProcess = getOpenkoreProcess(this.openkoreHome);
        EXECUTOR_SERVICE.submit(() -> {
            Scanner scanner = new Scanner(openkoreProcess.getErrorStream());

            // TODO: not sure why this for loop has to be here, but using waitFor does not work.
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
    public void close() throws IOException {

    }
}
