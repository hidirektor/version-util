# Version Util

[![](https://jitpack.io/v/hidirektor/version-util.svg)](https://jitpack.io/#hidirektor/version-util)

Version Util is a version management and update utility developed in Java.

## Features
- Fetching version information from GitHub
- File downloading with progress tracking

## Installation

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.hidirektor</groupId>
    <artifactId>version-util</artifactId>
    <version>v1.1.8</version>
</dependency>
```

### Gradle

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.hidirektor:version-util:v1.1.8'
}
```

## Usage

### Fetching the Latest Version
```java
String latestVersion = VersionUtil.getLatestVersion("owner", "repo");
System.out.println("Latest version: " + latestVersion);
```

### Checking Local Version
```java
String localVersion = VersionUtil.getLocalVersion("configNode", "versionKey");
System.out.println("Local version: " + localVersion);
```

### Version Comparison
```java
boolean isSame = VersionUtil.compareVersions("1.0.0", "1.0.0");
System.out.println("Are versions equal? " + isSame);
```

### Fetching Release Details
```java
ReleaseDetail detail = VersionUtil.getReleaseDetail("owner", "repo", "1.0.0");
System.out.println("Title: " + detail.getTitle());
System.out.println("Description: " + detail.getDescription());
System.out.println("Files: " + detail.getAssets());
```

### Fetching Latest Release Details
```java
ReleaseDetail latestDetail = VersionUtil.getLatestReleaseDetail("owner", "repo");
System.out.println("Title: " + latestDetail.getTitle());
System.out.println("Description: " + latestDetail.getDescription());
System.out.println("Files: " + latestDetail.getAssets());
```

### Downloading Files
#### Specific Version
```java
VersionUtil.downloadVersion("owner", "repo", "1.0.0", "./downloads", "example-file.jar");
```

#### Latest Version
```java
VersionUtil.downloadLatest("owner", "repo", "./downloads", "example-file.jar");
```

### File Downloading with ProgressBar
```java
private void startDownloadTask(String fileName, ProgressBar currentProgressBar, String repoName) {
    Task<Void> downloadTask = new Task<>() {
        @Override
        protected Void call() {
            try {
                DownloadProgressListener downloadListener = (bytesRead, totalBytes) -> {
                    Platform.runLater(() -> currentProgressBar.setProgress(0));

                    if (totalBytes > 0) {
                        double progress = (double) bytesRead / totalBytes;
                        Platform.runLater(() -> currentProgressBar.setProgress(progress));
                    } else {
                        Platform.runLater(() -> currentProgressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS)); // Indeterminate progress
                    }
                };

                VersionUtil.downloadLatestWithProgress(
                        Definitions.REPO_OWNER,
                        repoName,
                        Definitions.mainPath,
                        fileName,
                        downloadListener
                );
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
            return null;
        }
    };

    try {
        Thread downloadThread = new Thread(downloadTask);
        downloadThread.setDaemon(true);
        downloadThread.start();
        downloadThread.join();
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        e.printStackTrace();
        // Handle the interruption appropriately
    }
}
```

## License
This project is licensed under the [MIT License](LICENSE).