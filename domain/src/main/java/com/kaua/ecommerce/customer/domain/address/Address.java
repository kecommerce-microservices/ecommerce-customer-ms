package com.kaua.ecommerce.customer.domain.address;

import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.lib.domain.AggregateRoot;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import com.kaua.ecommerce.lib.domain.utils.InstantUtils;

import java.time.Instant;
import java.util.Optional;

public class Address extends AggregateRoot<AddressId> {

    private static final String SHOULD_NOT_BE_NULL = "should not be null";
    private static final String SHOULD_NOT_BE_EMPTY = "should not be empty";

    private Title title;
    private CustomerId customerId;
    private String zipCode;
    private String number;
    private String street;
    private String city;
    private String district;
    private String country;
    private String state;
    private String complement;
    private boolean isDefault;
    private final Instant createdAt;
    private Instant updatedAt;

    private Address(
            final AddressId aAddressId,
            final long aVersion,
            final Title aTitle,
            final CustomerId aCustomerId,
            final String aZipCode,
            final String aNumber,
            final String aStreet,
            final String aCity,
            final String aDistrict,
            final String aCountry,
            final String aState,
            final String aComplement,
            final boolean aIsDefault,
            final Instant aCreatedAt,
            final Instant aUpdatedAt
    ) {
        super(aAddressId, aVersion);
        this.setTitle(aTitle);
        this.setCustomerId(aCustomerId);
        this.setZipCode(aZipCode);
        this.setNumber(aNumber);
        this.setStreet(aStreet);
        this.setCity(aCity);
        this.setDistrict(aDistrict);
        this.setCountry(aCountry);
        this.setState(aState);
        this.setComplement(aComplement);
        this.setDefault(aIsDefault);
        this.createdAt = aCreatedAt;
        this.setUpdatedAt(aUpdatedAt);
    }

    public static Address newAddress(
            final Title aTitle,
            final CustomerId aCustomerId,
            final String aZipCode,
            final String aNumber,
            final String aStreet,
            final String aCity,
            final String aDistrict,
            final String aCountry,
            final String aState,
            final String aComplement,
            final boolean aIsDefault
    ) {
        final var aId = new AddressId(IdentifierUtils.generateNewUUID());
        final var aNow = InstantUtils.now();

        return new Address(
                aId,
                0,
                aTitle,
                aCustomerId,
                aZipCode,
                aNumber,
                aStreet,
                aCity,
                aDistrict,
                aCountry,
                aState,
                aComplement,
                aIsDefault,
                aNow,
                aNow
        );
    }

    public Address updateIsDefault(final boolean aIsDefault) {
        this.setDefault(aIsDefault);
        this.setUpdatedAt(InstantUtils.now());
        return this;
    }

    public static Address with(
            final AddressId aAddressId,
            final long aVersion,
            final Title aTitle,
            final CustomerId aCustomerId,
            final String aZipCode,
            final String aNumber,
            final String aStreet,
            final String aCity,
            final String aDistrict,
            final String aCountry,
            final String aState,
            final String aComplement,
            final boolean aIsDefault,
            final Instant aCreatedAt,
            final Instant aUpdatedAt
    ) {
        return new Address(
                aAddressId,
                aVersion,
                aTitle,
                aCustomerId,
                aZipCode,
                aNumber,
                aStreet,
                aCity,
                aDistrict,
                aCountry,
                aState,
                aComplement,
                aIsDefault,
                aCreatedAt,
                aUpdatedAt
        );
    }

    public Title getTitle() {
        return title;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getNumber() {
        return number;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public Optional<String> getComplement() {
        return Optional.ofNullable(complement);
    }

    public boolean isDefault() {
        return isDefault;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    private void setTitle(final Title title) {
        this.title = this.assertArgumentNotNull(title, "title", SHOULD_NOT_BE_NULL);
    }

    private void setCustomerId(final CustomerId customerId) {
        this.customerId = this.assertArgumentNotNull(customerId, "customerId", SHOULD_NOT_BE_NULL);
    }

    private void setZipCode(final String zipCode) {
        this.zipCode = this.assertArgumentNotEmpty(zipCode, "zipCode", SHOULD_NOT_BE_EMPTY);
    }

    private void setNumber(final String number) {
        this.number = this.assertArgumentNotEmpty(number, "number", SHOULD_NOT_BE_EMPTY);
    }

    private void setStreet(final String street) {
        this.street = this.assertArgumentNotEmpty(street, "street", SHOULD_NOT_BE_EMPTY);
    }

    private void setCity(final String city) {
        this.city = this.assertArgumentNotEmpty(city, "city", SHOULD_NOT_BE_EMPTY);
    }

    private void setDistrict(final String district) {
        this.district = this.assertArgumentNotEmpty(district, "district", SHOULD_NOT_BE_EMPTY);
    }

    private void setCountry(final String country) {
        this.country = this.assertArgumentNotEmpty(country, "country", SHOULD_NOT_BE_EMPTY);
    }

    private void setState(final String state) {
        this.state = this.assertArgumentNotEmpty(state, "state", SHOULD_NOT_BE_EMPTY);
    }

    private void setComplement(final String complement) {
        this.complement = complement;
    }

    private void setDefault(final boolean aDefault) {
        isDefault = aDefault;
    }

    private void setUpdatedAt(final Instant updatedAt) {
        this.updatedAt = this.assertArgumentNotNull(updatedAt, "updatedAt", SHOULD_NOT_BE_NULL);
    }

    @Override
    public String toString() {
        return "Address(" +
                "id=" + getId().value().toString() +
                ", title=" + title.value() +
                ", customerId=" + customerId.value().toString() +
                ", zipCode='" + zipCode + '\'' +
                ", number='" + number + '\'' +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", country='" + country + '\'' +
                ", state='" + state + '\'' +
                ", complement='" + getComplement().orElse(null) + '\'' +
                ", isDefault=" + isDefault +
                ", createdAt=" + createdAt +
                ", updatedAt=" + createdAt +
                ", version=" + getVersion() +
                ')';
    }
}
