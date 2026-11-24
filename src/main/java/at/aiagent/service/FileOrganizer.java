package at.aiagent.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class FileOrganizer {

    public Map<String, List<Path>> organizeByType(Path directory) throws IOException {

        if (!Files.exists(directory)) {
            throw new IOException("Der Ordner existiert nicht.");
        }

        if (!Files.isDirectory(directory)) {
            throw new IOException("Der Pfad ist kein Ordner.");
        }

        Map<String, List<Path>> organizedFiles = new LinkedHashMap<>();

        try (Stream<Path> paths = Files.list(directory)) {
            paths.filter(Files::isRegularFile)
                    .forEach(path -> {
                        String fileName = path.getFileName()
                                .toString()
                                .toLowerCase();

                        String category = getCategory(fileName);

                        organizedFiles.computeIfAbsent(
                                category,
                                key ->new ArrayList<>()
                        )
                                .add(path);
                    });
        }
        return organizedFiles;
    }

    private String getCategory(String fileName) {

        if (fileName.endsWith(".pdf")) {
            return "PDF";
        }

        if (fileName.endsWith(".txt")) {
            return "Text";
        }

        if (fileName.matches(".*\\.(jpg|jpeg|png|gif|bmp|webp)$")) {
            return "Bilder";
        }

        if (fileName.matches(".*\\.(doc|docx)$")) {
            return "Word";
        }

        if (fileName.matches(".*\\.(ppt|pptx|odp)$")) {
            return "Präsentation";
        }

        if (fileName.matches(".*\\.(zip|rar|7z|tar|gz)$")) {
            return "Archive";
        }

        if (fileName.matches(".*\\.(blend|obj|stl|fbx)$")) {
            return "3D-Dateien";
        }

        return "Sonstige";
    }
}
