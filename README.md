# Version Util

Version Util, Java ile geliştirilmiş bir sürüm kontrol ve güncelleme yardımcı aracıdır.

## Özellikler
- GitHub sürüm bilgisini çekme
- Dosya indirme ve ilerleme takibi
- Kısayol oluşturma (masaüstü ve başlangıç klasörleri)

## Kurulum

```xml
<dependency>
    <groupId>io.github.hidirektor</groupId>
    <artifactId>version-util</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Kullanım
```java
String latestVersion = VersionUtil.getLatestVersionFromGitHub("https://github.com/user/repo/releases/latest");
System.out.println("Son sürüm: " + latestVersion);
```

## Lisans
Bu proje [MIT Lisansı](LICENSE) altında lisanslanmıştır.