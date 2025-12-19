// ABOUTME: Unit tests for Money value type.
// ABOUTME: Verifies arithmetic, rounding, and comparison behavior.
package com.promoengine.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class MoneyTest {

    @Nested
    class Construction {
        @Test
        void shouldConstructFromString() {
            Money money = Money.euros("10.50");
            assertThat(money.amount()).isEqualByComparingTo("10.50");
        }

        @Test
        void shouldConstructFromDouble() {
            Money money = Money.euros(10.50);
            assertThat(money.amount()).isEqualByComparingTo("10.50");
        }

        @Test
        void shouldConstructFromBigDecimal() {
            Money money = Money.euros(new BigDecimal("10.50"));
            assertThat(money.amount()).isEqualByComparingTo("10.50");
        }

        @Test
        void shouldConstructFromCents() {
            Money money = Money.cents(1050);
            assertThat(money.amount()).isEqualByComparingTo("10.50");
        }

        @Test
        void shouldQuantizeToTwoDecimals() {
            Money money = Money.euros("10.12345");
            assertThat(money.amount()).isEqualByComparingTo("10.12");
        }

        @ParameterizedTest
        @CsvSource({
            "1.235, 1.24",  // rounds up
            "1.225, 1.23",  // half-up
            "1.224, 1.22"   // rounds down
        })
        void shouldUseRoundHalfUp(String input, String expected) {
            Money money = Money.euros(input);
            assertThat(money.amount()).isEqualByComparingTo(expected);
        }

        @Test
        void shouldRejectNullAmount() {
            assertThatThrownBy(() -> new Money(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
        }
    }

    @Nested
    class Arithmetic {
        @Test
        void shouldAddTwoAmounts() {
            Money m1 = Money.euros("10.50");
            Money m2 = Money.euros("5.25");
            Money result = m1.add(m2);
            assertThat(result).isEqualTo(Money.euros("15.75"));
        }

        @Test
        void shouldSubtractTwoAmounts() {
            Money m1 = Money.euros("10.50");
            Money m2 = Money.euros("5.25");
            Money result = m1.subtract(m2);
            assertThat(result).isEqualTo(Money.euros("5.25"));
        }

        @Test
        void shouldMultiplyByInteger() {
            Money m = Money.euros("1.50");
            Money result = m.multiply(3);
            assertThat(result).isEqualTo(Money.euros("4.50"));
        }

        @Test
        void shouldMultiplyByBigDecimal() {
            Money m = Money.euros("10.00");
            Money result = m.multiply(new BigDecimal("0.15"));
            assertThat(result).isEqualTo(Money.euros("1.50"));
        }

        @Test
        void shouldMultiplyByDecimalWithRounding() {
            Money m = Money.euros("10.01");
            Money result = m.multiply(new BigDecimal("0.15"));
            // 10.01 * 0.15 = 1.5015, rounds to 1.50
            assertThat(result).isEqualTo(Money.euros("1.50"));
        }

        @Test
        void shouldRejectNullInAddition() {
            Money m = Money.euros("10.00");
            assertThatThrownBy(() -> m.add(null))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        void shouldRejectNullInSubtraction() {
            Money m = Money.euros("10.00");
            assertThatThrownBy(() -> m.subtract(null))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        void shouldRejectNullInMultiplication() {
            Money m = Money.euros("10.00");
            assertThatThrownBy(() -> m.multiply((BigDecimal) null))
                .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    class Comparisons {
        @Test
        void shouldCompareEqual() {
            Money m1 = Money.euros("10.00");
            Money m2 = Money.euros("10.00");
            assertThat(m1).isEqualTo(m2);
            assertThat(m1.compareTo(m2)).isEqualTo(0);
        }

        @Test
        void shouldCompareLessThan() {
            Money m1 = Money.euros("10.00");
            Money m2 = Money.euros("20.00");
            assertThat(m1.compareTo(m2)).isLessThan(0);
            assertThat(m1.isLessThan(m2)).isTrue();
            assertThat(m1.isLessThanOrEqual(m2)).isTrue();
        }

        @Test
        void shouldCompareGreaterThan() {
            Money m1 = Money.euros("20.00");
            Money m2 = Money.euros("10.00");
            assertThat(m1.compareTo(m2)).isGreaterThan(0);
            assertThat(m1.isGreaterThan(m2)).isTrue();
            assertThat(m1.isGreaterThanOrEqual(m2)).isTrue();
        }

        @Test
        void shouldNotBeEqual() {
            Money m1 = Money.euros("10.00");
            Money m2 = Money.euros("20.00");
            assertThat(m1).isNotEqualTo(m2);
        }

        @Test
        void shouldRejectNullInComparison() {
            Money m = Money.euros("10.00");
            assertThatThrownBy(() -> m.compareTo(null))
                .isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    void shouldFormatAsEuros() {
        Money m = Money.euros("42.50");
        assertThat(m.toString()).isEqualTo("€42.50");
    }

    @Test
    void shouldHandleZeroAmount() {
        Money zero = Money.euros("0.00");
        assertThat(zero.amount()).isEqualByComparingTo("0.00");
    }
}
