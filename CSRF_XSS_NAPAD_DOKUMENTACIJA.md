# CSRF i XSS Napad - Dokumentacija

## 4.1. Napad

### Opis napada:

Kombinacija CSRF (Cross-Site Request Forgery) i XSS (Cross-Site Scripting) napada nad procesom dodavanja komentara na knjigu sa ID 1.

### Kako izvršiti napad:

1. Ulogovati se na `http://localhost:8080/login` (username: bruce, password: wayne)
2. Otvoriti `http://localhost:8081` u istom tabu
3. Kliknuti "Click here!" dugme
4. Napad će poslati zlonamerni komentar sa XSS payload-om

### XSS Payload:

```javascript
<script>alert("JSESSIONID: " + document.cookie);</script>
```

### Rezultat:

- CSRF napad dodaje komentar bez korisnikove svesti
- XSS payload prikazuje JSESSIONID kada se stranica knjige učita

## 4.2. Odbrana

### Implementirane zaštite:

#### CSRF zaštita:

- Omogućen Spring Security CSRF token
- Dodat CSRF token u HTML formu
- JavaScript šalje CSRF token u X-CSRF-TOKEN header-u

#### XSS zaštita:

- Korišćen `th:text` umesto `th:utext` za prikaz komentara
- Sprečava izvršavanje JavaScript koda u komentarima

### Fajlovi:

- `csrf-exploit/index.html` - CSRF napad
- `src/main/resources/templates/book.html` - Odbrana implementirana
- `src/main/java/com/zuehlke/securesoftwaredevelopment/config/SecurityConfig.java` - CSRF konfiguracija
