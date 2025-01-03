package me.t3sl4.util.version;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.t3sl4.util.version.exception.VersionException;
import me.t3sl4.util.version.model.ReleaseDetail;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

public class VersionUtil {

    private static final String GITHUB_API_URL = "https://api.github.com/repos/";

    public static String getLatestVersion(String owner, String repo) {
        String url = GITHUB_API_URL + owner + "/" + repo + "/releases/latest";
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(request)) {
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity);
                JsonObject json = JsonParser.parseString(result).getAsJsonObject();
                return json.get("tag_name").getAsString();
            }
        } catch (Exception e) {
            throw new VersionException("Release bulunamadı: " + e.getMessage());
        }
    }

    public static String getLocalVersion(String node, String prefKey) {
        return System.getProperty(node + "." + prefKey, "0.0.0");
    }

    public static boolean compareVersions(String version1, String version2) {
        return version1.equals(version2);
    }

    public static ReleaseDetail getReleaseDetail(String owner, String repo, String version) {
        String url = GITHUB_API_URL + owner + "/" + repo + "/releases/tags/" + version;
        return fetchReleaseDetail(url);
    }

    public static ReleaseDetail getLatestReleaseDetail(String owner, String repo) {
        String url = GITHUB_API_URL + owner + "/" + repo + "/releases/latest";
        return fetchReleaseDetail(url);
    }

    private static ReleaseDetail fetchReleaseDetail(String url) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(request)) {
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity);
                JsonObject json = JsonParser.parseString(result).getAsJsonObject();

                String title = json.get("name").getAsString();
                String description = json.get("body").getAsString();

                JsonArray assetsArray = json.getAsJsonArray("assets");
                List<String> assets = new ArrayList<>();
                for (JsonElement asset : assetsArray) {
                    JsonObject assetObject = asset.getAsJsonObject();
                    assets.add(assetObject.get("name").getAsString());
                };
                return new ReleaseDetail(title, description, assets);
            }
        } catch (Exception e) {
            throw new VersionException("Detaylar alınamadı: " + e.getMessage());
        }
    }

    public static void downloadVersion(String owner, String repo, String version, String destinationPath, String desiredFile) {
        downloadAsset(owner, repo, version, destinationPath, desiredFile);
    }

    public static void downloadLatest(String owner, String repo, String destinationPath, String desiredFile) {
        String latestVersion = getLatestVersion(owner, repo);
        downloadAsset(owner, repo, latestVersion, destinationPath, desiredFile);
    }

    private static void downloadAsset(String owner, String repo, String version, String destinationPath, String desiredFile) {
        String url = GITHUB_API_URL + owner + "/" + repo + "/releases/tags/" + version;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(request)) {
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity);
                JsonObject json = JsonParser.parseString(result).getAsJsonObject();
                JsonArray assetsArray = json.getAsJsonArray("assets");

                for (JsonElement asset : assetsArray) {
                    JsonObject assetObject = asset.getAsJsonObject();
                    String assetName = assetObject.get("name").getAsString();
                    if (desiredFile == null || desiredFile.equals(assetName)) {
                        String downloadUrl = assetObject.get("browser_download_url").getAsString();
                        downloadFile(downloadUrl, destinationPath + "/" + assetName);
                    }
                }
            }
        } catch (Exception e) {
            throw new VersionException("İndirme başarısız: " + e.getMessage());
        }
    }

    private static void downloadFile(String fileUrl, String destination) throws IOException {
        try (ReadableByteChannel rbc = Channels.newChannel(new URL(fileUrl).openStream());
             FileOutputStream fos = new FileOutputStream(destination)) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }
}