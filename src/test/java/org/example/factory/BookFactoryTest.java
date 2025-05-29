package org.example.factory;

import org.example.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// testy dla BookFactory - wzorzec Factory
class BookFactoryTest {

    private BookFactory bookFactory;

    @BeforeEach
    void setUp() {
        bookFactory = new BookFactory();
    }

    @Test
    void shouldCreatePhysicalBook() {
        // when
        AbstractBook book = bookFactory.createBook(BookType.PHYSICAL, "Title", "Author", 2, 3);

        // then
        assertThat(book).isInstanceOf(PhysicalBook.class);
        assertThat(book.getBookType()).isEqualTo("Physical book");
        assertThat(book.isDigital()).isFalse();
        assertThat(book.getTitle()).isEqualTo("Title");
        assertThat(book.getAuthor()).isEqualTo("Author");
    }

    @Test
    void shouldCreateEBook() {
        // when
        AbstractBook book = bookFactory.createBook(BookType.EBOOK, "Digital Title", "Digital Author", 5, 5);

        // then
        assertThat(book).isInstanceOf(EBook.class);
        assertThat(book.getBookType()).isEqualTo("eBook");
        assertThat(book.isDigital()).isTrue();
        assertThat(book.getTitle()).isEqualTo("Digital Title");
    }

    @Test
    void shouldCreateAudioBook() {
        // when
        AbstractBook book = bookFactory.createBook(BookType.AUDIOBOOK, "Audio Title", "Audio Author", 1, 2);

        // then
        assertThat(book).isInstanceOf(AudioBook.class);
        assertThat(book.getBookType()).isEqualTo("Audiobook");
        assertThat(book.isDigital()).isTrue();
        assertThat(book.getTitle()).isEqualTo("Audio Title");
    }

    @Test
    void shouldCreateBookFromStringType() {
        // when
        AbstractBook physicalBook = bookFactory.createBook("PHYSICAL", "Title1", "Author1", 1, 1);
        AbstractBook eBook = bookFactory.createBook("EBOOK", "Title2", "Author2", 1, 1);
        AbstractBook audioBook = bookFactory.createBook("AUDIOBOOK", "Title3", "Author3", 1, 1);

        // then
        assertThat(physicalBook).isInstanceOf(PhysicalBook.class);
        assertThat(eBook).isInstanceOf(EBook.class);
        assertThat(audioBook).isInstanceOf(AudioBook.class);
    }

    @Test
    void shouldCreatePhysicalBookForUnknownType() {
        // when - nieznany typ powinien utworzyc ksiazke fizyczna
        AbstractBook book = bookFactory.createBook("UNKNOWN_TYPE", "Title", "Author", 1, 1);

        // then
        assertThat(book).isInstanceOf(PhysicalBook.class);
        assertThat(book.getBookType()).isEqualTo("Physical book");
    }

    @Test
    void shouldCreateBookIgnoringCase() {
        // when - test ignorowania wielkosci liter
        AbstractBook book1 = bookFactory.createBook("physical", "Title1", "Author1", 1, 1);
        AbstractBook book2 = bookFactory.createBook("EBOOK", "Title2", "Author2", 1, 1);
        AbstractBook book3 = bookFactory.createBook("Audiobook", "Title3", "Author3", 1, 1);

        // then
        assertThat(book1).isInstanceOf(PhysicalBook.class);
        assertThat(book2).isInstanceOf(EBook.class);
        assertThat(book3).isInstanceOf(AudioBook.class);
    }

    @Test
    void shouldCreateDifferentTypesWithSameParameters() {
        // given
        String title = "Same Title";
        String author = "Same Author";
        Integer available = 2;
        Integer total = 3;

        // when - factory tworzy rozne typy z tymi samymi parametrami
        AbstractBook physical = bookFactory.createBook(BookType.PHYSICAL, title, author, available, total);
        AbstractBook ebook = bookFactory.createBook(BookType.EBOOK, title, author, available, total);
        AbstractBook audiobook = bookFactory.createBook(BookType.AUDIOBOOK, title, author, available, total);

        // then - obiekty maja te same dane ale rozne typy
        assertThat(physical.getTitle()).isEqualTo(ebook.getTitle()).isEqualTo(audiobook.getTitle());
        assertThat(physical.getClass()).isNotEqualTo(ebook.getClass()).isNotEqualTo(audiobook.getClass());
        
        // but different behaviors due to polymorphism
        assertThat(physical.isDigital()).isFalse();
        assertThat(ebook.isDigital()).isTrue();
        assertThat(audiobook.isDigital()).isTrue();
    }
}