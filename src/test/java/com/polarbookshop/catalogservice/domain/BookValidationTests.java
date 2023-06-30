package com.polarbookshop.catalogservice.domain;

import jakarta.annotation.Nonnull;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class BookValidationTests {
    static class IllegalValue {
        @SuppressWarnings("ConstantConditions")
        static @Nonnull <T> T sneakyNullReference() {
            return null;
        }

        static String sneakyBlankString() {
            return " ";
        }
    }

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void whenAllFieldsCorrectThenValidationSucceeds() {
        var book = new Book("1234567890", "Title", "Author", 9.90);

        var violations = validator.validate(book);

        assertThat(violations).isEmpty();
    }

    @Test
    void whenIsbnIsNullThenValidationFails() {
        var book = new Book(IllegalValue.sneakyNullReference(), "Title", "Author", 9.90);

        var violations = validator.validate(book);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The book ISBN must be defined.");
    }

    @Test
    void whenIsbnIsBlankThenValidationFails() {
        var book = new Book(IllegalValue.sneakyBlankString(), "Title", "Author", 9.90);

        var violations = validator.validate(book);

        assertThat(violations).hasSize(2);

        var messages = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
        assertThat(messages)
                .contains("The book ISBN must be defined.")
                .contains("The ISBN format must be valid.");
    }

    @Test
    void whenIsbnDefinedButIncorrectThenValidationFails() {
        var book = new Book("a123", "Title", "Author", 9.90);

        var violations = validator.validate(book);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The ISBN format must be valid.");
    }

    @Test
    void whenTitleIsNullThenValidationFails() {
        var book = new Book("1234567890", IllegalValue.sneakyNullReference(), "Author", 9.90);

        var violations = validator.validate(book);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The book title must be defined.");
    }

    @Test
    void whenTitleIsBlankThenValidationFails() {
        var book = new Book("1234567890", IllegalValue.sneakyBlankString(), "Author", 9.90);

        var violations = validator.validate(book);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The book title must be defined.");
    }

    @Test
    void whenAuthorIsNullThenValidationFails() {
        var book = new Book("1234567890", "Title", IllegalValue.sneakyNullReference(), 9.90);

        var violations = validator.validate(book);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The book author must be defined.");
    }

    @Test
    void whenAuthorIsBlankThenValidationFails() {
        var book = new Book("1234567890", "Title", IllegalValue.sneakyBlankString(), 9.90);

        var violations = validator.validate(book);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The book author must be defined.");
    }

    @Test
    void whenPriceIsNullThenValidationFails() {
        var book = new Book("1234567890", "Title", "Author", IllegalValue.sneakyNullReference());

        var violations = validator.validate(book);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The book price must be defined.");
    }

    @Test
    void whenPriceIsZeroThenValidationFails() {
        var book = new Book("1234567890", "Title", "Author", 0.0);

        var violations = validator.validate(book);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The book price must be greater than zero.");
    }

    @Test
    void whenPriceIsNegativeThenValidationFails() {
        var book = new Book("1234567890", "Title", "Author", -100.0);

        var violations = validator.validate(book);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The book price must be greater than zero.");
    }
}
