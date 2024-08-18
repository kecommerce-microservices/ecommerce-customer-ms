package com.kaua.ecommerce.customer.infrastructure.repositories;

import com.kaua.ecommerce.customer.application.repositories.CustomerRepository;
import com.kaua.ecommerce.customer.domain.customer.Customer;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.customer.domain.person.Document;
import com.kaua.ecommerce.customer.infrastructure.jdbc.DatabaseClient;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
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
public class CustomerJdbcRepository implements CustomerRepository {

    private static final Logger log = LoggerFactory.getLogger(CustomerJdbcRepository.class);

    private final DatabaseClient databaseClient;

    public CustomerJdbcRepository(final DatabaseClient databaseClient) {
        this.databaseClient = Objects.requireNonNull(databaseClient);
    }

    @Override
    public CustomerId nextId() {
        return new CustomerId(IdentifierUtils.generateNewUUID());
    }

    @Override
    public boolean existsByEmail(final String email) {
        final var aSql = "SELECT COUNT(*) FROM customers WHERE email = :email";
        return this.databaseClient.count(aSql, Map.of("email", email)) > 0;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Customer save(final Customer aCustomer) {
        log.debug("Creating a new customer: {}", aCustomer);
        create(aCustomer);
        log.info("Customer created: {}", aCustomer);

        return aCustomer;
    }

    private void create(final Customer aCustomer) {
        final var aSql = """
                INSERT INTO customers (id, version, idp_user_id, email, first_name, last_name, document_number, document_type, created_at, updated_at)
                VALUES (:id, (:version + 1), :idpUserId, :email, :firstName, :lastName, :documentNumber, :documentType, :createdAt, :updatedAt)
                """;
        executeUpdate(aSql, aCustomer);
    }

    private void executeUpdate(final String sql, final Customer aCustomer) {
        final var aParams = new HashMap<String, Object>();
        aParams.put("id", aCustomer.getId().value());
        aParams.put("version", aCustomer.getVersion());
        aParams.put("idpUserId", aCustomer.getUserId().value());
        aParams.put("email", aCustomer.getEmail().value());
        aParams.put("firstName", aCustomer.getName().firstName());
        aParams.put("lastName", aCustomer.getName().lastName());
        aParams.put("documentNumber", aCustomer.getDocument().map(Document::value).orElse(null));
        aParams.put("documentType", aCustomer.getDocument().map(Document::type).orElse(null));
        aParams.put("createdAt", Timestamp.from(aCustomer.getCreatedAt()));
        aParams.put("updatedAt", Timestamp.from(aCustomer.getUpdatedAt()));

        this.databaseClient.update(sql, aParams);
    }
}
