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

### Sürüm Bilgisi Çekme
```java
String latestVersion = VersionUtil.getLatestVersionFromGitHub("https://github.com/user/repo/releases/latest");
System.out.println("Son sürüm: " + latestVersion);
```

### Dosya İndirme ve Çalıştırma
```java
for (JsonElement asset : assetsArray) {
    JsonObject assetObject = asset.getAsJsonObject();
    String assetName = assetObject.get("name").getAsString();
    if (desiredFile == null || desiredFile.equals(assetName)) {
        String downloadUrl = assetObject.get("browser_download_url").getAsString();
        downloadFile(downloadUrl, destinationPath + "/" + assetName);
    }
}
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

