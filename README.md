# Version Util

Version Util, Java ile geliştirilmiş bir sürüm kontrol ve güncelleme yardımcı aracıdır.

## Özellikler
- GitHub sürüm bilgisini çekme
- Dosya indirme ve ilerleme takibi
- Kısayol oluşturma (masaüstü ve başlangıç klasörleri)
- Platforma göre dosya çalıştırma desteği (Windows, Unix/Linux, MacOS)
- JSON veri işleme

## Kurulum

```xml
<dependency>
    <groupId>io.github.hidirektor</groupId>
    <artifactId>version-util</artifactId>
    <version>1.0.0</version>
</dependency>
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

### Platforma Göre Dosya Çalıştırma
```java
try {
String os = System.getProperty("os.name").toLowerCase();

    if (os.contains("win") && hydraulicPath.endsWith(".exe")) {
        new ProcessBuilder("cmd.exe", "/c", hydraulicPath).start();
    } else if (os.contains("nix") || os.contains("nux")) {
        if (hydraulicPath.endsWith(".jar")) {
        new ProcessBuilder("java", "-jar", hydraulicPath).start();
        } else {
                System.err.println("Unsupported file type for Unix/Linux: " + hydraulicPath);
        }
                } else if (os.contains("mac")) {
        if (hydraulicPath.endsWith(".jar")) {
        new ProcessBuilder("java", "-jar", hydraulicPath).start();
        } else {
                System.err.println("Unsupported file type for MacOS: " + hydraulicPath);
        }
                } else {
                System.err.println("Unsupported OS or file type for: " + hydraulicPath);
    }

            GeneralUtil.minimizeToSystemTray(currentStage);

} catch (IOException e) {
        e.printStackTrace();
    System.err.println("Failed to execute hydraulic file: " + hydraulicPath);
}
```

## Lisans
Bu proje [MIT Lisansı](LICENSE) altında lisanslanmıştır.