# Test Napad i Odbrana - Uputstvo

## Testiranje CSRF i XSS napada

### 1. Testiranje bez odbrane (originalna verzija)

1. **Vrati originalnu verziju koda** (pre implementacije odbrane):

   ```bash
   git checkout HEAD -- src/main/java/com/zuehlke/securesoftwaredevelopment/config/SecurityConfig.java
   git checkout HEAD -- src/main/resources/templates/book.html
   git checkout HEAD -- src/main/java/com/zuehlke/securesoftwaredevelopment/repository/CommentRepository.java
   ```

2. **Pokreni aplikaciju**:

   ```bash
   mvn spring-boot:run
   ```

3. **Uloguj se** kao korisnik (korisničko ime: `admin`, lozinka: `admin`)

4. **Otvorí csrf-exploit/index.html** u browser-u

5. **Klikni "Click here!"** - napad će biti uspešan i prikazaće se JSESSIONID

### 2. Testiranje sa odbranom

1. **Primeni sve izmene za odbranu** (već implementirane)

2. **Pokreni aplikaciju**:

   ```bash
   mvn spring-boot:run
   ```

3. **Uloguj se** kao korisnik

4. **Otvorí csrf-exploit/index.html** u browser-u

5. **Klikni "Click here!"** - napad neće uspeti zbog CSRF zaštite

6. **Testiraj XSS zaštitu**:
   - Idi na stranicu knjige (npr. `/book?id=1`)
   - Pokušaj da dodaš komentar sa HTML/JS kodom
   - Kod će se prikazati kao tekst, a ne izvršiti

## Očekivani rezultati

### Bez odbrane:

- CSRF napad uspešan
- XSS kod se izvršava u komentarima
- JSESSIONID se prikazuje u alert-u

### Sa odbranom:

- CSRF napad ne uspeva (403 Forbidden greška)
- XSS kod se prikazuje kao običan tekst
- Aplikacija funkcioniše normalno
