// ABOUTME: Unit tests for Percentage value type.
// ABOUTME: Verifies range validation and decimal conversion.
package com.promoengine.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class PercentageTest {

    @Nested
    class Construction {
        @Test
        void shouldConstructFromString() {
            Percentage pct = Percentage.of("15.5");
            assertThat(pct.value()).isEqualByComparingTo("15.5");
        }

        @Test
        void shouldConstructFromDouble() {
            Percentage pct = Percentage.of(15.5);
            assertThat(pct.value()).isEqualByComparingTo("15.5");
        }

        @Test
        void shouldAcceptZero() {
            Percentage pct = Percentage.of("0");
            assertThat(pct.value()).isEqualByComparingTo("0");
        }

        @Test
        void shouldAcceptHundred() {
            Percentage pct = Percentage.of("100");
            assertThat(pct.value()).isEqualByComparingTo("100");
        }

        @Test
        void shouldRejectNegative() {
            assertThatThrownBy(() -> Percentage.of("-1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("between 0 and 100");
        }

        @Test
        void shouldRejectOverHundred() {
            assertThatThrownBy(() -> Percentage.of("101"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("between 0 and 100");
        }

        @Test
        void shouldRejectNull() {
            assertThatThrownBy(() -> new Percentage(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
        }
    }

    @Nested
    class Conversion {
        @Test
        void shouldConvertToDecimal() {
            Percentage pct = Percentage.of("15");
            assertThat(pct.asDecimal()).isEqualByComparingTo("0.15");
        }

        @Test
        void shouldConvertZeroToDecimal() {
            Percentage pct = Percentage.of("0");
            assertThat(pct.asDecimal()).isEqualByComparingTo("0.00");
        }

        @Test
        void shouldConvertHundredToDecimal() {
            Percentage pct = Percentage.of("100");
            assertThat(pct.asDecimal()).isEqualByComparingTo("1.00");
        }

        @Test
        void shouldConvertDecimalPercentageToDecimal() {
            Percentage pct = Percentage.of("12.5");
            assertThat(pct.asDecimal()).isEqualByComparingTo("0.125");
        }
    }

    @Test
    void shouldFormatWithPercentSymbol() {
        Percentage pct = Percentage.of("15.5");
        assertThat(pct.toString()).isEqualTo("15.5%");
    }
}
