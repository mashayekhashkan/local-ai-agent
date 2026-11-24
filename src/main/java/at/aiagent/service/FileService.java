package at.aiagent.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class FileService {

    public List<String> listFiles(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            throw new IOException("Der Ordner existiert nicht.");
        }

        if (!Files.isDirectory(directory)) {
            throw new IOException("Der angegebene Pfad ist kein Ordner.");
        }

        try (Stream<Path> paths = Files.list(directory)) {
            return paths
                    .map(path -> {
                        String type = Files.isDirectory(path)
                                ? "[ORDNER]" : "[DATEI]";
                        return type + path.getFileName();
                    })
                    .sorted()
                    .toList();
        }
    }

    public String readTextFile(Path file) throws IOException {

        if (!Files.exists(file)) {
            throw new IOException("Die Datei existiert nicht.");
        }

        if (!Files.isRegularFile(file)) {
            throw new IOException("Der Pfad keine Datei.");
        }

        return Files.readString(file);
    }

    public String readPdfFile(Path file) throws IOException {

        if (!Files.exists(file)) {
            throw new IOException("Die PDF-Datei existiert nicht.");
        }

        if (!Files.isRegularFile(file)) {
            throw new IOException("Der pfad ist keine Datei.");
        }

        try (PDDocument document = Loader.loadPDF(file.toFile())) {

            PDFTextStripper pdfTextStripper = new PDFTextStripper();

            return pdfTextStripper.getText(document);
        }
    }

    public String readFile(Path file) throws IOException {

        String fileName = file.getFileName()
                .toString()
                .toLowerCase();

        if (fileName.endsWith(".txt")) {
            return readTextFile(file);
        }

        if (fileName.endsWith(".pdf")) {
            return readPdfFile(file);
        }

        throw new IOException(
                "Dieser DateiTyp wird noch nicht unterstützt"
        );
    }

}
