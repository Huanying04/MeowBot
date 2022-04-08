package net.nekomura.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    public static String readFile(File path) throws IOException {
        FileInputStream fis = new FileInputStream(path);
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(path), StandardCharsets.UTF_8));) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    public static void writeFile(File path, String content) throws IOException {
        FileOutputStream fos = new FileOutputStream(path);
        try (OutputStreamWriter osw =  new OutputStreamWriter(fos,
                StandardCharsets.UTF_8)) {
            osw.write(content);
        }
    }

    public static List<File> getAllFilesInDir(final File folder) {
        if (!folder.exists()) return null;
        List<File> list = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isFile()) {
                list.add(fileEntry);
            }
        }
        return list;
    }

    public static File getResourceFile(String path) throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(path);
        if (is == null)
            return null;
        File file = File.createTempFile("cat_confuse", "jpg");
        Files.copy(is, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return file;
    }

}
