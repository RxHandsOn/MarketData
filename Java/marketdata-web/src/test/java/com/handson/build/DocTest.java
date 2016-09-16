package com.handson.build;


import org.assertj.core.util.Files;
import org.junit.Test;
import org.pegdown.PegDownProcessor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DocTest {

    @Test
    public void should_generate_readme() throws IOException {
        File readmeFile = findFile("README.md");
        String readme = Files.contentOf(readmeFile, "UTF-8");
        String html = generateHtml(readme);
        writeHtml("README.html", html);
    }

    @Test
    public void should_generate_plan() throws IOException {
        File planFile = findFile("PLAN.md");
        String plan = Files.contentOf(planFile, "UTF-8");
        String html = generateHtml(plan);
        writeHtml("PLAN.html", html);
    }

    private String generateHtml(String readme) {
        String html = new PegDownProcessor().markdownToHtml(readme);
        html = html.replace("<code>java", "<pre>").replace("</code>", "</pre>");
        return html;
    }

    private void writeHtml(String fileName, String content) throws IOException {
        String testTarget = getClass().getResource("/").getFile();
        FileOutputStream outputStream = new FileOutputStream(new File(testTarget, "../" + fileName));
        content = "<html><head><meta charset=\"UTF-8\"></head>" + content + "</html>";
        outputStream.write(content.getBytes("UTF-8"));
        Files.flushAndClose(outputStream);
    }

    private File findFile(String name) {
        File currentFolder = Files.currentFolder();
        final File result;
        try {
            if (currentFolder.getCanonicalPath().contains("marketdata-web")) {
                // for eclipse
                result = new File(currentFolder, "../" + name);
            } else {
                // for intellij
                result = new File(currentFolder, name);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

}
