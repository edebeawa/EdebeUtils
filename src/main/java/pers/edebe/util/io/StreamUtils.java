package pers.edebe.util.io;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

public final class StreamUtils {
    public static ByteArrayOutputStream toByteArrayOutputStream(InputStream stream) throws IOException {
        try (BufferedInputStream inputStream = new BufferedInputStream(stream); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            int result;
            while ((result = inputStream.read()) != -1) {
                outputStream.write(result);
            }
            return outputStream;
        }
    }

    public static String toString(InputStream stream) throws IOException {
        return StreamUtils.toByteArrayOutputStream(stream).toString();
    }

    public static String toString(InputStream stream, String charsetName) throws IOException {
        return StreamUtils.toByteArrayOutputStream(stream).toString(charsetName);
    }

    public static String toString(InputStream stream, Charset charset) throws IOException {
        return StreamUtils.toByteArrayOutputStream(stream).toString(charset);
    }

    public static byte[] toByteArray(InputStream stream) throws IOException {
        return StreamUtils.toByteArrayOutputStream(stream).toByteArray();
    }

    public static byte[] toByteArray(char[] chars, Charset charset) {
        CharBuffer charBuffer = CharBuffer.allocate(chars.length);
        charBuffer.put(chars);
        charBuffer.flip();
        ByteBuffer byteBuffer = charset.encode(charBuffer);
        return byteBuffer.array();
    }

    public static void writeAllLine(BufferedWriter writer, List<String> lines) throws IOException {
        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }
    }
}