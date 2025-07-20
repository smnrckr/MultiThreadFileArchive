# MultiThreadFileArchive

## Proje Açıklaması

**MultiThreadFileArchive**, bir klasördeki `.txt` dosyalarını çoklu iş parçacığı (multithreading) ile işleyen, ardından bu dosyaları arşivleyip (zip), tekrar açan (unzip) ve işlem sürelerini raporlayan bir Java uygulamasıdır. Proje, dosya okuma, analiz, arşivleme ve arşivden çıkarma işlemlerini eşzamanlı (concurrent) olarak gerçekleştirerek yüksek verimlilik sağlar.

---

## Özellikler

- **Çoklu İş Parçacığı (Multithreading):** Aynı anda birden fazla dosya işlenir, böylece işlem süresi kısalır.
- **Semaphore ile Eşzamanlılık Kontrolü:** Aynı anda çalışan iş parçacığı sayısı sınırlandırılır.
- **Dosya Arşivleme (Zip):** İşlenen dosyalar bir arşiv dosyasında (zip) toplanır.
- **Arşivden Çıkarma (Unzip):** Arşivlenen dosyalar tekrar bir klasöre çıkarılır.
- **İstatistik Toplama:** Her dosya için analiz sonuçları ve işlem süreleri raporlanır.
- **Kapsamlı Hata Yönetimi:** İşlem sırasında oluşabilecek hatalar kullanıcıya bildirilir.

---

## Dizin Yapısı

```
MultiThreadFileArchive/
│
├── input/                # İşlenecek .txt dosyalarının bulunduğu klasör
├── output/
│   └── files.zip         # Arşivlenen dosyaların çıktısı
├── unzipped_output/      # Arşivden çıkarılan dosyaların konduğu klasör
├── src/
│   ├── Main.java         # Uygulamanın giriş noktası
│   ├── model/
│   │   └── FileStatistics.java
│   ├── service/
│   │   ├── FileAnalyzerService.java
│   │   ├── FileReaderService.java
│   │   └── ResultCollector.java
│   ├── thread/
│   │   ├── FileArchiverUnzip.java
│   │   ├── FileArchiverZip.java
│   │   ├── FileProcessorThread.java
│   │   └── ThreadMonitor.java
│   ├── util/
│   │   └── SafeFileUtils.java
│   └── worker/
│       └── Worker.java
├── README.md
```

---

## Kurulum

1. **Java Kurulumu:**  
   Java 8 veya üzeri bir sürümün sisteminizde kurulu olduğundan emin olun.

2. **Projeyi Klonlayın:**
   ```sh
   git clone https://github.com/smnrckr/MultiThreadFileArchive.git
   cd MultiThreadFileArchive
   ```

3. **Gerekli Klasörleri Oluşturun:**  
   `input/` klasörüne işlenecek `.txt` dosyalarını ekleyin.  
   `output/` ve `unzipped_output/` klasörleri otomatik olarak oluşturulur veya elle oluşturabilirsiniz.

4. **Projeyi Derleyin:**
   ```sh
   javac -d out -sourcepath src src/Main.java
   ```

5. **Projeyi Çalıştırın:**
   ```sh
   java -cp out Main
   ```

---

## Kullanım

1. `input/` klasörüne işlemek istediğiniz `.txt` dosyalarını ekleyin.
2. Programı başlatın.
3. Program, her dosyayı ayrı bir iş parçacığında işler, sonuçları toplar, ardından tüm dosyaları `output/files.zip` olarak arşivler.
4. Arşivlenen dosyalar, `unzipped_output/` klasörüne tekrar çıkarılır.
5. Konsolda işlem süreleri ve analiz sonuçları raporlanır.

---

## Örnek Konsol Çıktısı
<img width="957" height="1156" alt="Ekran görüntüsü 2025-07-20 205914" src="https://github.com/user-attachments/assets/3878aead-9f0a-46c1-bf52-60eb6a2c07b8" />

---

## Sınıf ve Paket Açıklamaları

- **Main.java:** Uygulamanın ana akışını yönetir. Zip açma, dosya analiz etme, arşivleme ve süre hesaplama gibi tüm işlemlerin sırasıyla başlatıldığı yerdir.
- **model.FileStatistics:** Dosya analizi sonucunda satır ve karakter sayısı gibi istatistikleri tutar.
- **service.FileAnalyzerService:** Dosya analiz işlemlerini (satır, kelime, karakter sayımı vb.) gerçekleştirir.
- **service.FileReaderService:** Dosyanın içeriğini güvenli bir şekilde okur ve satırlara ayırır.
- **service.ResultCollector:** Analiz sonuçlarını toplar ve raporlar.
- **thread.FileProcessorThread:** Her bir dosya için iş parçacığı oluşturur ve dosya analizini başlatır.
- **thread.FileArchiverZip / FileArchiverUnzip:** Dosyaları arşivler ve arşivden çıkarır.
- **thread.ThreadMonitor:** Tüm iş parçacıklarının durumunu ve tamamlanma sürelerini takip eder.
- **util.SafeFileUtils:** Dosya işlemlerinde güvenli yardımcı metotlar sağlar.
- **worker.Worker:** Tek bir dosya üzerinde analiz işlemini gerçekleştiren sınıftır. FileAnalyzerService üzerinden analiz yapar ve sonucu ResultCollector ile toplar.


---

## Geliştiriciler

1. Mete Kerem Berk
2. Fatma Nur Kurt
3. Elif Keleş
4. Ömer Gün
5. Semanur Çakır

---
