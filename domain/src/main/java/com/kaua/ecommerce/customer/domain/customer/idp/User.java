package com.kaua.ecommerce.customer.domain.customer.idp;

import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.customer.domain.person.Email;
import com.kaua.ecommerce.customer.domain.person.Name;
import com.kaua.ecommerce.lib.domain.validation.AssertionConcern;

public class User implements AssertionConcern {

    private static final String SHOULD_NOT_BE_NULL = "should not be null";

    private final UserId userId;
    private CustomerId customerId;
    private Name name;
    private Email email;
    private String password;

    private User(
            final UserId aUserId,
            final CustomerId aCustomerId,
            final Name aName,
            final Email aEmail
    ) {
        this.userId = aUserId;
        this.setCustomerId(aCustomerId);
        this.setName(aName);
        this.setEmail(aEmail);
    }

    private User(
            final UserId aUserId,
            final CustomerId aCustomerId,
            final Name aName,
            final Email aEmail,
            final String aPassword
    ) {
        this(aUserId, aCustomerId, aName, aEmail);
        this.setPassword(this.assertArgumentNotEmpty(aPassword, "password", "should not be empty for new users"));
    }

    public static User newUser(final CustomerId aCustomerId, final Name aName, final Email aEmail, final String aPassword) {
        return new User(UserId.empty(), aCustomerId, aName, aEmail, aPassword);
    }

    public static User with(final UserId aUserId, final CustomerId aCustomerId, final Name aName, final Email aEmail) {
        return new User(aUserId, aCustomerId, aName, aEmail);
    }

    public UserId getUserId() {
        return userId;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public Name getName() {
        return name;
    }

    public Email getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    private void setCustomerId(final CustomerId customerId) {
        this.customerId = this.assertArgumentNotNull(customerId, "customerId", SHOULD_NOT_BE_NULL);
    }

    private void setName(final Name name) {
        this.name = this.assertArgumentNotNull(name, "name", SHOULD_NOT_BE_NULL);
    }

    private void setEmail(final Email email) {
        this.email = this.assertArgumentNotNull(email, "email", SHOULD_NOT_BE_NULL);
    }

    private void setPassword(final String password) {
        this.password = password;
    }
}
