package io.github.hidirektor.versionutil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class VersionUtil {

    public static String getLatestVersionFromGitHub(String releaseUrl) {
        try {
            HttpResponse<Void> response = httpHead(releaseUrl);

            if (response.statusCode() == 302) { // 302 Redirect
                String redirectedUrl = response.headers().firstValue("location").orElse(null);
                if (redirectedUrl != null) {
                    return extractTagFromURL(redirectedUrl);
                } else {
                    System.err.println("Yönlendirme URL'si alınamadı.");
                    return null;
                }
            } else if (response.statusCode() == 200) {
                return extractTagFromURL(releaseUrl);
            } else {
                System.err.println("GitHub sürüm bilgisi alınamadı: HTTP " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String extractTagFromURL(String url) {
        String tagPrefix = "/releases/tag/";
        int tagIndex = url.indexOf(tagPrefix);
        if (tagIndex == -1) {
            return null;
        }
        return url.substring(tagIndex + tagPrefix.length());
    }

    private static HttpResponse<Void> httpHead(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
        return client.send(request, HttpResponse.BodyHandlers.discarding());
    }

    public static void downloadFile(String fileUrl, File destination) throws IOException {
        System.out.println("Dosya indiriliyor: " + fileUrl);
        URL url = new URL(fileUrl);

        try (InputStream in = url.openStream();
             FileOutputStream out = new FileOutputStream(destination)) {
            int fileSize = url.openConnection().getContentLength();
            byte[] buffer = new byte[1024];
            int bytesRead;
            int downloaded = 0;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                downloaded += bytesRead;
                double progress = (double) downloaded / fileSize;
            }
        }
        System.out.println("İndirme tamamlandı: " + destination.getAbsolutePath());
    }

    public static void deleteIfExists(File file) {
        if (file.exists() && file.delete()) {
            System.out.println("Mevcut dosya silindi: " + file.getAbsolutePath());
        }
    }
}