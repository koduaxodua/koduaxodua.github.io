# GeoSwipe IME (ქართული Swipe Keyboard Prototype)

ეს არის Android Input Method Editor (IME) პროტოტიპი, რომლის მიზანია ქართულ QWERTY-ზე swipe-to-type გამოცდილების აშენება.

## მთავარი იდეა
- ქართული QWERTY ლეაუტის მოდელი
- Swipe ტრაექტორიის მიხედვით სიტყვის გამოცნობა
- გრძელი დაჭერის სიმბოლოები: **თ, ძ, ჭ, ჟ, ღ**
- ლექსიკონზე დაფუძნებული top-N შეთავაზებები

## სტრუქტურა
- `app/src/main/java/io/koduaxodua/geoswipe/ime` — IME service
- `app/src/main/java/io/koduaxodua/geoswipe/decoder` — Swipe decoder, ლეაუტი, ლექსიკონი
- `app/src/main/res/xml/method.xml` — IME subtype კონფიგი

## Android-ზე დაყენება (შემდგომი ნაბიჯები)
1. Android Studio-თ გახსენი `android-geoswipe-ime`
2. გაუშვი Gradle sync
3. Build APK
4. დააყენე ტელეფონზე (`adb install` ან პირდაპირ Studio-დან)
5. Settings → System → Keyboard → Enable **GeoSwipe (ქართული)**

## 5-6 საათიანი სამუშაოს მიზნობრივი როუდმეპი
1. რეალური key hitbox-ების ამოღება UI-დან
2. gesture sampling-ის გაუმჯობესება (smoothing + resampling)
3. დიდ ლექსიკონზე scorer tuning
4. long-press letters-ის კონტექსტური წესები
5. suggestion strip (3-5 სიტყვა) + tap არჩევა

## შენიშვნა
პროექტი არის საწყისი MVP scaffold. იდეალური ხარისხისთვის საჭიროა დიდი კორპუსი, language model reranking და real-device latency tuning.
