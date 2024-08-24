package com.kaua.ecommerce.customer.domain.customer;

import com.kaua.ecommerce.customer.domain.customer.idp.UserId;
import com.kaua.ecommerce.customer.domain.person.Document;
import com.kaua.ecommerce.customer.domain.person.Email;
import com.kaua.ecommerce.customer.domain.person.Name;
import com.kaua.ecommerce.lib.domain.AggregateRoot;
import com.kaua.ecommerce.lib.domain.utils.InstantUtils;

import java.time.Instant;
import java.util.Optional;

public class Customer extends AggregateRoot<CustomerId> {

    private static final String SHOULD_NOT_BE_NULL = "should not be null";

    private UserId userId;
    private Email email;
    private Name name;
    private Document document;
    private final Instant createdAt;
    private Instant updatedAt;

    private Customer(
            final CustomerId aCustomerId,
            final long aVersion,
            final UserId aUserId,
            final Email aEmail,
            final Name aName,
            final Document aDocument,
            final Instant aCreatedAt,
            final Instant aUpdatedAt
    ) {
        super(aCustomerId, aVersion);
        this.setUserId(aUserId);
        this.setEmail(aEmail);
        this.setName(aName);
        this.setDocument(aDocument);
        this.createdAt = aCreatedAt;
        this.setUpdatedAt(aUpdatedAt);
    }

    public static Customer newCustomer(
            final CustomerId aCustomerId,
            final UserId aUserId,
            final Email aEmail,
            final Name aName
    ) {
        final var aNow = InstantUtils.now();
        return new Customer(aCustomerId, 0, aUserId, aEmail, aName, null, aNow, aNow);
    }

    public static Customer with(
            final CustomerId aCustomerId,
            final long aVersion,
            final UserId aUserId,
            final Email aEmail,
            final Name aName,
            final Document aDocument,
            final Instant aCreatedAt,
            final Instant aUpdatedAt
    ) {
        return new Customer(aCustomerId, aVersion, aUserId, aEmail, aName, aDocument, aCreatedAt, aUpdatedAt);
    }

    public Customer updateDocument(final Document aDocument) {
        this.setDocument(aDocument);
        this.setUpdatedAt(InstantUtils.now());
        return this;
    }

    public UserId getUserId() {
        return userId;
    }

    public Email getEmail() {
        return email;
    }

    public Name getName() {
        return name;
    }

    public Optional<Document> getDocument() {
        return Optional.ofNullable(document);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    private void setUserId(final UserId userId) {
        this.userId = this.assertArgumentNotNull(userId, "userId", SHOULD_NOT_BE_NULL);
    }

    private void setEmail(final Email email) {
        this.email = this.assertArgumentNotNull(email, "email", SHOULD_NOT_BE_NULL);
    }

    private void setName(final Name name) {
        this.name = this.assertArgumentNotNull(name, "name", SHOULD_NOT_BE_NULL);
    }

    private void setDocument(final Document document) {
        this.document = document;
    }

    private void setUpdatedAt(final Instant updatedAt) {
        this.updatedAt = this.assertArgumentNotNull(updatedAt, "updatedAt", SHOULD_NOT_BE_NULL);
    }

    @Override
    public String toString() {
        return "Customer(" +
                "customerId=" + getId().value().toString() +
                ", userId=" + getUserId().value().toString() +
                ", email=" + email.value() +
                ", name=" + name.fullName() +
                ", document=" + getDocument().map(Document::type).orElse(null) +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", version=" + getVersion() +
                ')';
    }
}
