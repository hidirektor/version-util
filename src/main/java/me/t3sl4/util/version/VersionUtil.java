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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Utility class for managing version-related operations, such as fetching release details
 * and downloading assets from GitHub repositories.
 */
public class VersionUtil {

    private static final String GITHUB_API_URL = "https://api.github.com/repos/";

    /**
     * Retrieves the latest release version tag from a specified GitHub repository.
     *
     * @param owner The owner of the GitHub repository.
     * @param repo  The name of the GitHub repository.
     * @return The tag name of the latest release.
     * @throws VersionException If the release cannot be found or an error occurs during the request.
     */
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

    /**
     * Retrieves the local version from system properties.
     *
     * @param node    The node or category of the property.
     * @param prefKey The specific key for the desired property.
     * @return The local version as a string; defaults to "0.0.0" if not found.
     */
    public static String getLocalVersion(String node, String prefKey) {
        String version = Preferences.userRoot().node(node).get(prefKey, null);

        return version != null ? version : "0.0.0";
    }

    /**
     * Compares two version strings for equality.
     *
     * @param version1 The first version string.
     * @param version2 The second version string.
     * @return {@code true} if both versions are equal; {@code false} otherwise.
     */
    public static boolean compareVersions(String version1, String version2) {
        return version1.equals(version2);
    }

    /**
     * Fetches the release details for a specific version from a GitHub repository.
     *
     * @param owner   The owner of the GitHub repository.
     * @param repo    The name of the GitHub repository.
     * @param version The version tag of the release.
     * @return A {@link ReleaseDetail} object containing the release information.
     * @throws VersionException If the release details cannot be retrieved.
     */
    public static ReleaseDetail getReleaseDetail(String owner, String repo, String version) {
        String url = GITHUB_API_URL + owner + "/" + repo + "/releases/tags/" + version;
        return fetchReleaseDetail(url);
    }

    /**
     * Fetches the latest release details from a GitHub repository.
     *
     * @param owner The owner of the GitHub repository.
     * @param repo  The name of the GitHub repository.
     * @return A {@link ReleaseDetail} object containing the latest release information.
     * @throws VersionException If the release details cannot be retrieved.
     */
    public static ReleaseDetail getLatestReleaseDetail(String owner, String repo) {
        String url = GITHUB_API_URL + owner + "/" + repo + "/releases/latest";
        return fetchReleaseDetail(url);
    }

    /**
     * Downloads a specific version of an asset from a GitHub repository to the specified destination.
     *
     * @param owner           The owner of the GitHub repository.
     * @param repo            The name of the GitHub repository.
     * @param version         The version tag of the release.
     * @param destinationPath The local file path where the asset will be saved.
     * @param desiredFile     The name of the desired asset file to download; if {@code null}, all assets are downloaded.
     * @throws VersionException If the download fails.
     */
    public static void downloadVersion(String owner, String repo, String version, String destinationPath, String desiredFile) {
        downloadAsset(owner, repo, version, destinationPath, desiredFile);
    }

    /**
     * Downloads a specific version of an asset from a GitHub repository to the specified destination.
     *
     * @param owner           The owner of the GitHub repository.
     * @param repo            The name of the GitHub repository.
     * @param destinationPath The local file path where the asset will be saved.
     * @param desiredFile     The name of the desired asset file to download; if {@code null}, all assets are downloaded.
     * @throws VersionException If the download fails.
     */
    public static void downloadLatest(String owner, String repo, String destinationPath, String desiredFile) {
        String latestVersion = getLatestVersion(owner, repo);
        downloadAsset(owner, repo, latestVersion, destinationPath, desiredFile);
    }

    /**
     * Downloads a specific version of an asset from a GitHub repository with progress tracking.
     *
     * @param owner           The owner of the GitHub repository.
     * @param repo            The name of the GitHub repository.
     * @param version         The version tag of the release.
     * @param destinationPath The local file path where the asset will be saved.
     * @param desiredFile     The name of the desired asset file to download; if {@code null}, all assets are downloaded.
     * @param listener        A {@link DownloadProgressListener} to receive progress updates.
     * @throws VersionException If the download fails.
     */
    public static void downloadVersionWithProgress(String owner, String repo, String version, String destinationPath, String desiredFile, DownloadProgressListener listener) {
        downloadAssetWithProgress(owner, repo, version, destinationPath, desiredFile, listener);
    }

    /**
     * Downloads a specific version's asset from a GitHub repository with progress tracking.
     *
     * @param owner           the owner of the GitHub repository
     * @param repo            the name of the GitHub repository
     * @param destinationPath the local file path where the asset will be saved
     * @param desiredFile     the name of the desired asset file to download; if {@code null}, all assets are downloaded
     * @param listener        a {@link DownloadProgressListener} to receive progress updates; may be {@code null}
     * @throws VersionException if the download fails
     */
    public static void downloadLatestWithProgress(String owner, String repo, String destinationPath, String desiredFile, DownloadProgressListener listener) {
        String latestVersion = getLatestVersion(owner, repo);
        downloadAssetWithProgress(owner, repo, latestVersion, destinationPath, desiredFile, listener);
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

    private static void downloadAssetWithProgress(String owner, String repo, String version, String destinationPath, String desiredFile, DownloadProgressListener listener) {
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
                        downloadFileWithProgress(downloadUrl, destinationPath + "/" + assetName, listener);
                    }
                }
            }
        } catch (Exception e) {
            throw new VersionException("Download failed: " + e.getMessage());
        }
    }

    private static void downloadFileWithProgress(String fileUrl, String destination, DownloadProgressListener listener) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        int contentLength = connection.getContentLength();
        if (contentLength < 0) {
            throw new IOException("Unable to determine file size.");
        }

        try (InputStream inputStream = connection.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(destination)) {

            byte[] buffer = new byte[4096];
            long totalBytesRead = 0;
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                if (listener != null) {
                    listener.onProgress(totalBytesRead, contentLength);
                }
            }
        } finally {
            connection.disconnect();
        }
    }
}