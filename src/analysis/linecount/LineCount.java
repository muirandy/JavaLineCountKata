package analysis.linecount;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

public class LineCount {
    private boolean alreadyInBlock;

    public int countLines(File file) {
        try {
            Stream<String> lines = Files.lines(file.toPath());
            return count(lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    int count(Stream<String> lines) {
        long count = lines.filter(line -> shouldCount(line.trim())).count();
        return new Long(count).intValue();
    }

    private boolean shouldCount(String line) {
        if (lineIsComment(line)) {
            return false;
        }
        return !lineIsEmpty(removeBlockComment(line));
    }

    private String removeBlockComment(String line) {
        int startIndex = line.indexOf("/*");
        if (startIndex == 0) {
            alreadyInBlock = true;
            return removeBlockComment(line.substring(startIndex + 2).trim());
        }

        if (alreadyInBlock) {
            int endIndex = commentEnd(line);
            if (endIndex >= 0) {
                alreadyInBlock = false;
                return removeBlockComment(line.substring(endIndex + 2).trim());
            }
            return "";
        }

        return line.trim();
    }

    private int commentEnd(String line) {
        return line.indexOf("*/");
    }

    private boolean lineIsComment(String line) {
        return line.startsWith("//");
    }

    private boolean lineIsEmpty(String line) {
        return line.trim().isEmpty();
    }
}
