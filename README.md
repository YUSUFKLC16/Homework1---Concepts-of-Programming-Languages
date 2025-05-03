Homework1 – Concepts of Programming Languages

This is a homework project for a course on Concepts of Programming Languages. It demonstrates basic parsing using a custom LR parser implementation in Java.

Bu projede sıfırdan çalışan bir LR(1) parser geliştirdik. Öne çıkanlar:

Action ve Goto tabloları  okundu ve uygun veri yapılarında saklandı.
>`Grammar.txt` dosyasındaki kurallar `Production` nesneleri olarak belleğe yüklendi.
>Her bir input dosyası sırayla işlenerek:
>Stack, input ve yapılan işlem (action) adım adım takip edildi.
>Gerekli durumlarda shift, reduce, accept ya da error işlemleri yapıldı.
>Parse işlemi tamamlandığında parse tree oluşturularak görsel biçimde çıktı verildi.
>Tüm sonuçlar `target/output/` dizinine `outputX.txt` (örneğin `output1.txt`, `output2.txt` ...) formatında kaydedildi.

Kısaca:
> Bu proje yalnızca çalışmıyor, öğretiyor, izliyor, çözümlüyor ve belgeleyerek çıktı veriyor :)))

1-Requirements

. Java JDK 17 or higher
. Maven 3.6+ (optional but recommended)
. IntelliJ IDEA (or any IDE of your choice)

2-Getting Started

You can download or clone the repository to your local machine:

Option: Clone using Git
git clone https://github.com/YUSUFKLC16/Homework1---Concepts-of-Programming-Languages.git
cd Homework1---Concepts-of-Programming-Languages


