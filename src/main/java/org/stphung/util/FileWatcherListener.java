package org.stphung.util;

import java.util.List;

public interface FileWatcherListener {
    void fileChanged(List<String> newLines);
}
