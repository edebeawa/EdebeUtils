package pers.edebe.util.base;

import pers.edebe.util.io.StreamUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class FileUtils {
    public static byte[] getByteArray(File file) throws IOException {
        try (FileInputStream stream = new FileInputStream(file)) {
            return StreamUtils.toByteArray(stream);
        }
    }

    public static byte[] getByteArray(Path path) throws IOException {
        return getByteArray(path.toFile());
    }

    public static boolean isFileSuffixEquals(String name, String suffix) {
        return StringUtils.toLowerCase(name.substring(name.lastIndexOf(".") + 1)).equals(StringUtils.toLowerCase(suffix));
    }

    public static boolean isFileSuffixEquals(File file, String suffix) {
        return isFileSuffixEquals(file.getName(), suffix);
    }

    public static boolean isFileSuffixEquals(Path path, String suffix) {
        return isFileSuffixEquals(path.toFile(), suffix);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean isFileHeaderEquals(InputStream stream, byte[] magic) {
        try {
            int length = magic.length;
            byte[] buffer = new byte[length];
            stream.read(buffer, 0, length);
            return Arrays.equals(buffer, magic);
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isFileHeaderEquals(File file, byte[] magic) {
        try (FileInputStream stream = new FileInputStream(file)) {
            return isFileHeaderEquals(stream, magic);
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isFileHeaderEquals(Path path, byte[] magic) {
        return isFileHeaderEquals(path.toFile(), magic);
    }

    public static boolean isFileTypeEquals(ZipFile file, ZipEntry entry, String suffix, byte[] magic) {
        try {
            return !entry.isDirectory() && isFileSuffixEquals(entry.getName(), suffix) && isFileHeaderEquals(file.getInputStream(entry), magic);
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isFileTypeEquals(File file, String suffix, byte[] magic) {
        return !file.isDirectory() && isFileSuffixEquals(file, suffix) && isFileHeaderEquals(file, magic);
    }

    public static boolean isFileTypeEquals(Path path, String suffix, byte[] magic) {
        return isFileTypeEquals(path.toFile(), suffix, magic);
    }

    public static File findLibFile(URL url, BiFunction<Path, String, File> function) throws IOException {
        Path path = ClassUtils.getPath(url);
        File file;
        if (FileType.JAR.isThisFileType(path)) {
            String filepath = path.toString();
            int index = filepath.lastIndexOf(File.separator);
            path = Path.of(filepath.substring(0, index));
            file = function.apply(path, filepath.substring(index + 1));
            Files.createDirectories(path);
            if (!file.exists()) {
                if (file.createNewFile()) {
                    try (InputStream inputStream = url.openStream(); FileOutputStream outputStream = new FileOutputStream(file)) {
                        outputStream.write(StreamUtils.toByteArray(inputStream));
                    }
                } else {
                    throw new FileNotFoundException();
                }
            }
        } else {
            file = path.toFile();
        }
        return file;
    }
}