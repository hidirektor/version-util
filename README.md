# Version Util

[![](https://jitpack.io/v/hidirektor/version-util.svg)](https://jitpack.io/#hidirektor/version-util)

Version Util, Java ile geliştirilmiş bir sürüm kontrol ve güncelleme yardımcı aracıdır.

## Özellikler
- GitHub sürüm bilgisini çekme
- Dosya indirme ve ilerleme takibi
- Kısayol oluşturma (masaüstü ve başlangıç klasörleri)
- Platforma göre dosya çalıştırma desteği (Windows, Unix/Linux, MacOS)
- JSON veri işleme

## Kurulum

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
<version>v1.0.4</version>
</dependency>
```

### Gradle

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.hidirektor:version-util:1.0.0'
}
```

## Kullanım

### En Son Sürümü Alma
```java
String latestVersion = VersionUtil.getLatestVersion("owner", "repo");
System.out.println("En son sürüm: " + latestVersion);
```

### Yerel Sürüm Kontrolü
```java
String localVersion = VersionUtil.getLocalVersion("configNode", "versionKey");
System.out.println("Yerel sürüm: " + localVersion);
```

### Sürüm Karşılaştırma
```java
boolean isSame = VersionUtil.compareVersions("1.0.0", "1.0.0");
System.out.println("Sürümler eşit mi? " + isSame);
```

### Yayın Detaylarını Alma
```java
ReleaseDetail detail = VersionUtil.getReleaseDetail("owner", "repo", "1.0.0");
System.out.println("Başlık: " + detail.getTitle());
        System.out.println("Açıklama: " + detail.getDescription());
        System.out.println("Dosyalar: " + detail.getAssets());
```

### En Son Yayın Detaylarını Alma
```java
ReleaseDetail latestDetail = VersionUtil.getLatestReleaseDetail("owner", "repo");
System.out.println("Başlık: " + latestDetail.getTitle());
        System.out.println("Açıklama: " + latestDetail.getDescription());
        System.out.println("Dosyalar: " + latestDetail.getAssets());
```

### Dosya İndirme
#### Belirli Sürüm
```java
VersionUtil.downloadVersion("owner", "repo", "1.0.0", "./downloads", "example-file.jar");
```

#### En Son Sürüm
```java
VersionUtil.downloadLatest("owner", "repo", "./downloads", "example-file.jar");
```

### ProgressBar ile Dosya İndirme
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

## Lisans
Bu proje [MIT Lisansı](LICENSE) altında lisanslanmıştır.