// ABOUTME: Unit tests for PricingContext.
// ABOUTME: Verifies construction and customer tag membership.
package com.promoengine.pricing;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class PricingContextTest {

    @Test
    void shouldConstructWithValidFields() {
        Instant now = Instant.parse("2025-12-18T10:00:00Z");
        PricingContext context = new PricingContext(
            now,
            "online",
            "CUST001",
            Set.of("gold", "regular")
        );

        assertThat(context.now()).isEqualTo(now);
        assertThat(context.channel()).isEqualTo("online");
        assertThat(context.customerId()).isEqualTo("CUST001");
        assertThat(context.customerTags()).containsExactlyInAnyOrder("gold", "regular");
    }

    @Test
    void shouldCheckCustomerTagMembership() {
        PricingContext context = new PricingContext(
            Instant.now(),
            "online",
            "CUST001",
            Set.of("gold", "regular")
        );

        assertThat(context.hasTag("gold")).isTrue();
        assertThat(context.hasTag("regular")).isTrue();
        assertThat(context.hasTag("platinum")).isFalse();
    }

    @Test
    void shouldHandleEmptyCustomerTags() {
        PricingContext context = new PricingContext(
            Instant.now(),
            "online",
            "CUST001",
            Set.of()
        );

        assertThat(context.customerTags()).isEmpty();
        assertThat(context.hasTag("any")).isFalse();
    }

    @Test
    void shouldRejectNullNow() {
        assertThatThrownBy(() -> new PricingContext(
            null,
            "online",
            "CUST001",
            Set.of()
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Now cannot be null");
    }

    @Test
    void shouldRejectNullChannel() {
        assertThatThrownBy(() -> new PricingContext(
            Instant.now(),
            null,
            "CUST001",
            Set.of()
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Channel cannot be null or empty");
    }

    @Test
    void shouldRejectEmptyChannel() {
        assertThatThrownBy(() -> new PricingContext(
            Instant.now(),
            "",
            "CUST001",
            Set.of()
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Channel cannot be null or empty");
    }

    @Test
    void shouldRejectNullCustomerId() {
        assertThatThrownBy(() -> new PricingContext(
            Instant.now(),
            "online",
            null,
            Set.of()
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Customer ID cannot be null or empty");
    }

    @Test
    void shouldRejectNullCustomerTags() {
        assertThatThrownBy(() -> new PricingContext(
            Instant.now(),
            "online",
            "CUST001",
            null
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Customer tags cannot be null");
    }

    @Test
    void shouldBeImmutable() {
        PricingContext context = new PricingContext(
            Instant.now(),
            "online",
            "CUST001",
            Set.of("gold")
        );

        assertThatThrownBy(() -> context.customerTags().add("platinum"))
            .isInstanceOf(UnsupportedOperationException.class);
    }
}
