package org.stphung;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileWatcher {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final File file;
    private List<String> knownFileLines;
    private final List<FileWatcherListener> listeners;

    public FileWatcher(File file) throws FileNotFoundException {
        this.file = file;
        this.knownFileLines = getFileLines(file);
        this.listeners = Lists.newArrayList();
        executorService.submit((Runnable) () -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                    List<String> fileLines = getFileLines(file);
                    if (fileLines.size() > this.knownFileLines.size()) {
                        List<String> newLines = fileLines.subList(this.knownFileLines.size(), fileLines.size());
                        listeners.forEach(listener -> listener.fileChanged(newLines));
                        this.knownFileLines = fileLines;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void addListener(FileWatcherListener listener) {
        this.listeners.add(listener);
    }

    private List<String> getFileLines(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(file);
        ImmutableList.Builder<String> fileLinesBuilder = ImmutableList.builder();
        while (sc.hasNextLine()) {
            fileLinesBuilder.add(sc.nextLine());
        }
        return fileLinesBuilder.build();
    }
}
