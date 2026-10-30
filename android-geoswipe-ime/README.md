# GeoSwipe IME (ქართული Swipe Keyboard Prototype)

ეს არის Android Input Method Editor (IME) პროექტი, რომლის მიზანია ქართულ QWERTY-ზე swipe-to-type გამოცდილების პრაქტიკულად გამოსაყენებელ დონეზე მიყვანა.

## რა სრულყო ამ ვერსიამ
- ქართული QWERTY ლეაუტის გაფართოებული მოდელი (base + long-press სიმბოლოები)
- long-press ასოების მხარდაჭერა: **თ, ძ, ჭ, ჟ, ღ**
- გაუმჯობესებული swipe decoder:
  - gesture normalization
  - nearest-key sequence signature
  - path distance + sequence distance + endpoint scoring
  - long-press evidence penalty
- 3-პოზიციანი suggestion strip + tap-ით არჩევა
- ძირითადი action ღილაკები: Backspace / Space / Enter
- fallback ერთასოიანი შეყვანა (თუ swipe-ს შედეგი ვერ მოიძებნა)
- გაფართოებული საწყისი ქართული ლექსიკონი
- unit test-ები long-press ranking-ისთვის და top-N ქცევისთვის

## სტრუქტურა
- `app/src/main/java/io/koduaxodua/geoswipe/ime` — IME service და input interaction
- `app/src/main/java/io/koduaxodua/geoswipe/decoder` — layout model, dictionary, decoder
- `app/src/main/res/layout/input_view.xml` — UI (suggestions + swipe surface + action keys)
- `app/src/main/res/xml/method.xml` — IME subtype კონფიგი

## Android-ზე დაყენება
1. Android Studio-თ გახსენი `android-geoswipe-ime`
2. გაუშვი Gradle sync
3. Build APK
4. დააყენე ტელეფონზე (`adb install` ან Studio-დან)
5. Settings → System → Keyboard → Enable **GeoSwipe (ქართული)**

## შემდეგი ნაბიჯები (საბოლოო ხარისხისთვის)
1. რეალური key hitbox-ების კალიბრაცია მრავალ მოწყობილობაზე
2. დიდ კორპუსზე დაფუძნებული frequency/ranking მოდელი
3. სიტყვების პერსონალური ლექსიკონი და autocorrection
4. latency optimization (sampling, allocations, caching)
5. multi-word gesture და punctuation-aware typing
