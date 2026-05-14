package com.mdeditor.document;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {

    public static String read(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    public static void write(File file, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        }
    }

    public static String getExtension(File file) {
        String name = file.getName();
        int dot = name.lastIndexOf('.');
        return (dot == -1) ? "" : name.substring(dot).toLowerCase();
    }

    public static boolean isMarkdownFile(File file) {
        String ext = getExtension(file);
        return ext.equals(".md") || ext.equals(".markdown") || ext.equals(".mdown") || ext.equals(".txt");
    }
}
