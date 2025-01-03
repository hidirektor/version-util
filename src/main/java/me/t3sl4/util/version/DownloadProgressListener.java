package me.t3sl4.util.version;

public interface DownloadProgressListener {
    /**
     * Called periodically as bytes are downloaded.
     *
     * @param bytesRead     The total number of bytes downloaded so far.
     * @param totalBytes    The total size of the file in bytes.
     *                      If the total size is unknown, this value will be -1.
     */
    void onProgress(long bytesRead, long totalBytes);
}