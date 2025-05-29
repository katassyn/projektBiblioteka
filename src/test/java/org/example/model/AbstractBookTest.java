package org.example.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// testy dla polimorfizmu - rozne implementacje AbstractBook
class AbstractBookTest {

    @Test
    void shouldDemonstratePolymorphismWithPhysicalBook() {
        // given
        AbstractBook book = new PhysicalBook("Physical Book", "Author", 3, 3);

        // when & then - polimorfizm w akcji
        assertThat(book.getBookType()).isEqualTo("Physical book");
        assertThat(book.isDigital()).isFalse();
        assertThat(book.getDisplayInfo()).contains("Physical copy");
    }

    @Test
    void shouldDemonstratePolymorphismWithEBook() {
        // given
        AbstractBook book = new EBook("Digital Book", "Author", 5, 5);

        // when & then - polimorfizm w akcji
        assertThat(book.getBookType()).isEqualTo("eBook");
        assertThat(book.isDigital()).isTrue();
        assertThat(book.getDisplayInfo()).contains("Digital copy");
    }

    @Test
    void shouldDemonstratePolymorphismWithAudioBook() {
        // given
        AbstractBook book = new AudioBook("Audio Book", "Author", 2, 2);

        // when & then - polimorfizm w akcji
        assertThat(book.getBookType()).isEqualTo("Audiobook");
        assertThat(book.isDigital()).isTrue();
        assertThat(book.getDisplayInfo()).contains("Audio format");
    }

    @Test
    void shouldBehaveDifferentlyBasedOnConcreteType() {
        // given - rozne typy ksiazek
        AbstractBook physicalBook = new PhysicalBook("Book 1", "Author", 1, 1);
        AbstractBook eBook = new EBook("Book 2", "Author", 1, 1);
        AbstractBook audioBook = new AudioBook("Book 3", "Author", 1, 1);

        // when & then - kazdy typ zachowuje sie inaczej (polimorfizm)
        assertThat(physicalBook.isDigital()).isFalse();
        assertThat(eBook.isDigital()).isTrue();
        assertThat(audioBook.isDigital()).isTrue();

        assertThat(physicalBook.getDisplayInfo()).contains("Physical");
        assertThat(eBook.getDisplayInfo()).contains("Digital");
        assertThat(audioBook.getDisplayInfo()).contains("Audio");
    }

    @Test
    void shouldWorkWithPolymorphicArray() {
        // given - tablica polimorficzna
        AbstractBook[] books = {
                new PhysicalBook("Physical", "Author", 1, 1),
                new EBook("Digital", "Author", 1, 1),
                new AudioBook("Audio", "Author", 1, 1)
        };

        // when & then - polimorficzne wywolanie metod
        int digitalCount = 0;
        for (AbstractBook book : books) {
            if (book.isDigital()) {
                digitalCount++;
            }
        }

        assertThat(digitalCount).isEqualTo(2); // eBook i AudioBook sa cyfrowe
    }

    @Test
    void shouldSetAndGetBasicProperties() {
        // given
        AbstractBook book = new PhysicalBook("Test Title", "Test Author", 2, 3);

        // when
        book.setGenre("Fantasy");
        book.setPublicationYear(2023);

        // then
        assertThat(book.getTitle()).isEqualTo("Test Title");
        assertThat(book.getAuthor()).isEqualTo("Test Author");
        assertThat(book.getGenre()).isEqualTo("Fantasy");
        assertThat(book.getPublicationYear()).isEqualTo(2023);
        assertThat(book.getAvailableCopies()).isEqualTo(2);
        assertThat(book.getTotalCopies()).isEqualTo(3);
    }
}