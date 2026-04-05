package com.mergeodt.app;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class App {

    private static String currentDirectory;

    public static void main(String[] args) {
        boolean pageBreak = false;
        ArrayList<String> arguments = new ArrayList<>(Arrays.asList(args));
        currentDirectory = arguments.get(0);
        arguments.remove(0);
        if (arguments.get(0).equals("-pb")) {
            pageBreak = true;
            arguments.remove(0);
        }
        if (!arguments.isEmpty()) {
            File outputFile = new File(currentDirectory, arguments.get(arguments.size() - 1).replace(".odt", ".html"));
            arguments.remove(arguments.size() - 1);
            boolean first = true;
            Element newBody = new Element("body");
            Document newDoc = null;
            for (String arg : arguments) {
                try {
                    serialCommand(new String[] {"libreoffice", "--headless", "--convert-to", "html", arg});
                    File current = new File(currentDirectory, arg.replace(".odt", ".html"));
                    Document doc = Jsoup.parse(current, "UTF-8");
                    if (first) {
                        newDoc = doc.clone();
                        newBody = newDoc.select("body").first();
                        first = false;
                    } else {
                        Element oldBody = doc.select("body").first().clone();
                        if (pageBreak) {
                            String style = oldBody.children().first().attributes().get("style");
                            style += "; page-break-before: always";
                            oldBody.children().first().attributes().remove("style");
                            oldBody.children().first().attributes().add("style", style);
                        }
                        for (Element child : oldBody.children()) {
                            newBody.appendChild(child);
                        }
                    }
                    serialCommand(new String[] {"rm", arg.replace(".odt", ".html")});
                } catch (Exception e) {
                    System.out.println("Parsing HTML error");
                    e.printStackTrace();
                }
            }
            try {
                FileWriter fileWriter = new FileWriter(outputFile);
                PrintWriter printWriter = new PrintWriter(fileWriter);
                printWriter.print(newDoc.outerHtml());
                printWriter.close();
                serialCommand(new String[] {"libreoffice", "--headless", "--convert-to", "odt", outputFile.getPath()});
                serialCommand(new String[] {"rm", outputFile.getPath()});
            } catch (Exception e) {
                System.out.println("Exporting to ODT error");
                e.printStackTrace();
            }
        }
    }

    public static void serialCommand(String[] command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command); 
            processBuilder.directory(new File(currentDirectory));
            Process process = processBuilder.start();
            process.waitFor();
        } catch (Exception e) {
            System.out.println("Command error");
            e.printStackTrace();
        }
    }
}
