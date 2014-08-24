package org.stphung;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Set;

public class FileMonitor {
    private final File file;

    private final Set<String> lines;

    public FileMonitor(File file) throws FileNotFoundException {
        this.file = file;
        this.lines = this.getLines();
    }

    public Set<String> getLines() throws FileNotFoundException {
        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        Scanner sc = new Scanner(this.file);
        while (sc.hasNextLine()) {
            builder.add(sc.nextLine());
        }
        return builder.build();
    }

    public Set<String> getNewLines() throws FileNotFoundException {
        return Sets.difference(this.getLines(), this.lines);
    }
}
