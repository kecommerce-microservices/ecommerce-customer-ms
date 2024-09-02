package com.kaua.ecommerce.customer.infrastructure.repositories;

import com.kaua.ecommerce.customer.application.repositories.AddressRepository;
import com.kaua.ecommerce.customer.domain.address.Address;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.customer.infrastructure.jdbc.DatabaseClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Repository
public class AddressJdbcRepository implements AddressRepository {

    private static final Logger log = LoggerFactory.getLogger(AddressJdbcRepository.class);

    private static final String CUSTOMER_COLUMN = "customerId";

    private final DatabaseClient databaseClient;

    public AddressJdbcRepository(final DatabaseClient databaseClient) {
        this.databaseClient = Objects.requireNonNull(databaseClient);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Address save(final Address aAddress) {
        log.debug("Creating a new address: {}", aAddress);
        create(aAddress);
        log.info("Address created: {}", aAddress);

        aAddress.incrementVersion();
        return aAddress;
    }

    @Override
    public int countByCustomerId(final CustomerId aCustomerId) {
        final var aSql = "SELECT COUNT(*) FROM addresses WHERE customer_id = :customerId";
        return this.databaseClient.count(aSql, Map.of(CUSTOMER_COLUMN, aCustomerId.value()));
    }

    @Override
    public boolean existsByCustomerIdAndIsDefaultTrue(final CustomerId aCustomerId) {
        final var aSql = "SELECT COUNT(*) FROM addresses WHERE customer_id = :customerId AND is_default = true";
        return this.databaseClient.count(aSql, Map.of(CUSTOMER_COLUMN, aCustomerId.value())) > 0;
    }

    private void create(final Address aAddress) {
        final var aSql = """
                INSERT INTO addresses (id, version, title, customer_id, zip_code, number, street, city, district, country, state, complement, is_default, created_at, updated_at)
                VALUES (:id, (:version + 1), :title, :customerId, :zipCode, :number, :street, :city, :district, :country, :state, :complement, :isDefault, :createdAt, :updatedAt)
                """;
        executeUpdate(aSql, aAddress);
    }

    private void executeUpdate(final String sql, final Address aAddress) {
        final var aParams = new HashMap<String, Object>();
        aParams.put("id", aAddress.getId().value());
        aParams.put("version", aAddress.getVersion());
        aParams.put("title", aAddress.getTitle().value());
        aParams.put(CUSTOMER_COLUMN, aAddress.getCustomerId().value());
        aParams.put("zipCode", aAddress.getZipCode());
        aParams.put("number", aAddress.getNumber());
        aParams.put("street", aAddress.getStreet());
        aParams.put("city", aAddress.getCity());
        aParams.put("district", aAddress.getDistrict());
        aParams.put("country", aAddress.getCountry());
        aParams.put("state", aAddress.getState());
        aParams.put("complement", aAddress.getComplement().orElse(null));
        aParams.put("isDefault", aAddress.isDefault());
        aParams.put("createdAt", Timestamp.from(aAddress.getCreatedAt()));
        aParams.put("updatedAt", Timestamp.from(aAddress.getUpdatedAt()));

        this.databaseClient.update(sql, aParams);
    }
}
