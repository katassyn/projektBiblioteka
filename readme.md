# Bookstore Application

## Opis projektu

Aplikacja księgarni to system zarządzania książkami z funkcjonalnością wypożyczeń, zbudowany przy użyciu Spring Boot. Projekt implementuje wszystkie wymagane funkcjonalności zgodnie z wymogami projektowymi.

## Funkcjonalności

### Zarządzanie użytkownikami (RBAC)

- **USER**: Przeglądanie książek, wyszukiwanie, wypożyczanie, przeglądanie historii wypożyczeń
- **ADMIN**: Wszystkie funkcje użytkownika + zarządzanie książkami, użytkownikami i wypożyczeniami!

### Zarządzanie książkami

- Dodawanie, edytowanie, usuwanie książek (tylko admin)
- Wyszukiwanie książek po tytule, autorze, gatunku
- Przeglądanie dostępnych książek
- Polimorfizm - obsługa różnych typów książek (fizyczne, e-booki, audiobooki)

### System wypożyczeń

- Wypożyczanie książek przez użytkowników
- Zwracanie książek
- Historia wypożyczeń
- Zarządzanie przeterminowanymi wypożyczeniami (admin)

## Architektura i wzorce projektowe

### Polimorfizm

Projekt implementuje polimorfizm poprzez abstrakcyjną klasę `AbstractBook` i jej konkretne implementacje:

- `PhysicalBook` - książki fizyczne
- `EBook` - książki elektroniczne
- `AudioBook` - audiobooki

Każdy typ książki ma różne zachowanie w metodach `getBookType()`, `isDigital()` i `getDisplayInfo()`.

### Factory

Klasa `BookFactory` służy do tworzenia odpowiednich instancji książek na podstawie typu:

```java
@Component
public class BookFactory {
    public AbstractBook createBook(BookType bookType, String title, String author, 
                                  Integer availableCopies, Integer totalCopies) {
        switch (bookType) {
            case PHYSICAL: return new PhysicalBook(title, author, availableCopies, totalCopies);
            case EBOOK: return new EBook(title, author, availableCopies, totalCopies);
            case AUDIOBOOK: return new AudioBook(title, author, availableCopies, totalCopies);
            default: throw new IllegalArgumentException("Nieznany typ książki: " + bookType);
        }
    }
}
```

### Repository

Wszystkie operacje na bazie danych realizowane są poprzez repozytoria Spring Data JPA:

- `UserRepository`
- `BookRepository`
- `BorrowingRepository`
## Struktura bazy danych

### Diagram ERD
![[diagram 1.png]]
### Tabele

#### users

- `id` (BIGSERIAL, PK)
- `username` (VARCHAR(50), UNIQUE)
- `password` (VARCHAR(255))
- `email` (VARCHAR(255), UNIQUE)
- `first_name` (VARCHAR(50))
- `last_name` (VARCHAR(50))
- `role` (VARCHAR(20)) - USER/ADMIN

#### books

- `id` (BIGSERIAL, PK)
- `title` (VARCHAR(255))
- `author` (VARCHAR(255))
- `publication_year` (INTEGER)
- `genre` (VARCHAR(50))
- `available_copies` (INTEGER)
- `total_copies` (INTEGER)
- `book_type` (VARCHAR(20)) - PHYSICAL/EBOOK/AUDIOBOOK

#### borrowings

- `id` (BIGSERIAL, PK)
- `user_id` (BIGINT, FK)
- `book_id` (BIGINT, FK)
- `status` (VARCHAR(20)) - RESERVED/BORROWED/RETURNED/OVERDUE
- `borrow_date` (DATE)
- `due_date` (DATE)
- `return_date` (DATE)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

## Instrukcja uruchomienia
### Uruchomienie z Dockerem

1. Sklonuj repozytorium:

```bash
git clone https://github.com/katassyn/projektBiblioteka
cd bookstore
```

2. Uruchom aplikację z Docker Compose:

```bash
docker-compose up -d
```

### Główne endpointy

#### Uwierzytelnianie

- `POST /api/auth/register` - rejestracja użytkownika
- `POST /api/auth/login` - logowanie

#### Książki

- `GET /api/books` - lista wszystkich książek
- `GET /api/books/{id}` - szczegóły książki
- `POST /api/books` - dodanie książki (admin)
- `PUT /api/books/{id}` - edycja książki (admin)
- `DELETE /api/books/{id}` - usunięcie książki (admin)
- `GET /api/books/search?q={term}` - wyszukiwanie książek

#### Wypożyczenia

- `POST /api/borrowings/borrow/{bookId}` - wypożyczenie książki
- `POST /api/borrowings/return/{borrowingId}` - zwrot książki
- `GET /api/borrowings/my-history` - historia wypożyczeń
- `GET /api/borrowings/my-active` - aktywne wypożyczenia

#### Polimorfizm

- `GET /api/poly/books/display` - książki z polimorficznymi informacjami
- `GET /api/poly/books/stats` - statystyki książek cyfrowych vs fizycznych

## Przykłady użycia

### Rejestracja użytkownika
![[Pasted image 20250529164620.png]]
![[Pasted image 20250529164610.png]]

### Logowanie
![[Pasted image 20250529164635.png]]

### Przeglądanie książek
![[Pasted image 20250529164749.png]]
![[Pasted image 20250529164800.png]]
### Wypożyczanie książki
![[Pasted image 20250529164843.png]]
![[Pasted image 20250529170443.png]]
## Domyślni użytkownicy

Aplikacja zawiera dwóch domyślnych użytkowników:
### Administrator

- **Login**: `admin`
- **Hasło**: `admin123`
- **Email**: `admin@bookstore.com`
- **Rola**: ADMIN
### Użytkownik testowy

- **Login**: `user`
- **Hasło**: `admin123`
- **Email**: `user@bookstore.com`
- **Rola**: USER

## Testowanie

### Uruchomienie testów

```bash
mvn test
```

### Pokrycie kodu

Projekt zawiera testy jednostkowe i integracyjne zapewniające pokrycie kodu na poziomie 80%+.
![[Pasted image 20250529165309.png]]
## Struktura projektu

```
src/
├── main/
│   ├── java/org/example/
│   │   ├── config/          # Konfiguracja Spring Security, Swagger
│   │   ├── controller/      # Kontrolery REST
│   │   ├── dataTransfer/    # DTOs
│   │   ├── factory/         # Factory Pattern
│   │   ├── model/           # Encje JPA
│   │   ├── repository/      # Repozytoria Spring Data
│   │   └── service/         # Logika biznesowa
│   └── resources/
│       ├── db/migration/    # Migracje Flyway
│       └── application.properties
└── test/                    # Testy jednostkowe i integracyjne
```
## Funkcjonalności bezpieczeństwa

- **Szyfrowanie haseł** - BCrypt
- **Uwierzytelnianie** - HTTP Basic Auth
- **Autoryzacja** - Spring Security z rolami
- **Walidacja danych** - Bean Validation
