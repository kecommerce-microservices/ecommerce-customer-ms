package com.kaua.ecommerce.customer.infrastructure.repositories;

import com.kaua.ecommerce.customer.application.repositories.CustomerRepository;
import com.kaua.ecommerce.customer.domain.customer.Customer;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.customer.domain.customer.idp.UserId;
import com.kaua.ecommerce.customer.domain.person.Document;
import com.kaua.ecommerce.customer.domain.person.Email;
import com.kaua.ecommerce.customer.domain.person.Name;
import com.kaua.ecommerce.customer.domain.person.Telephone;
import com.kaua.ecommerce.customer.infrastructure.jdbc.DatabaseClient;
import com.kaua.ecommerce.customer.infrastructure.jdbc.JdbcUtils;
import com.kaua.ecommerce.customer.infrastructure.jdbc.RowMap;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import com.kaua.ecommerce.lib.infrastructure.exceptions.ConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

@Repository
public class CustomerJdbcRepository implements CustomerRepository {

    private static final Logger log = LoggerFactory.getLogger(CustomerJdbcRepository.class);

    private static final String EMAIL_COLUMN = "email";

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
        return this.databaseClient.count(aSql, Map.of(EMAIL_COLUMN, email)) > 0;
    }

    @Override
    public boolean existsByDocument(final String documentNumber) {
        final var aSql = "SELECT COUNT(*) FROM customers WHERE document_number = :documentNumber";
        return this.databaseClient.count(aSql, Map.of("documentNumber", documentNumber)) > 0;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Customer save(final Customer aCustomer) {
        if (aCustomer.getVersion() == 0) {
            log.debug("Creating a new customer: {}", aCustomer);
            create(aCustomer);
            log.info("Customer created: {}", aCustomer);
        } else {
            log.debug("Updating customer: {}", aCustomer);
            update(aCustomer);
            log.info("Customer updated: {}", aCustomer);
        }

        // in this part, increment version, or search in database again on update or on return of save method or keep the object with old version in memory
        aCustomer.incrementVersion();
        return aCustomer;
    }

    @Override
    public Optional<Customer> customerOfId(final CustomerId customerId) {
        final var aSql = "SELECT * FROM customers WHERE id = :id";
        return this.databaseClient.queryOne(aSql, Map.of("id", customerId.value().toString()), customerMapper());
    }

    private void create(final Customer aCustomer) {
        final var aSql = """
                INSERT INTO customers (id, version, idp_user_id, email, first_name, last_name, document_number, document_type, phone_number, created_at, updated_at)
                VALUES (:id, (:version + 1), :idpUserId, :email, :firstName, :lastName, :documentNumber, :documentType, :phoneNumber, :createdAt, :updatedAt)
                """;
        executeUpdate(aSql, aCustomer);
    }

    private void update(final Customer aCustomer) {
        final var aSql = """
                UPDATE customers
                SET
                    version = :version + 1,
                    idp_user_id = :idpUserId,
                    email = :email,
                    first_name = :firstName,
                    last_name = :lastName,
                    document_number = :documentNumber,
                    document_type = :documentType,
                    phone_number = :phoneNumber,
                    updated_at = :updatedAt
                WHERE id = :id AND version = :version
                """;

        if (executeUpdate(aSql, aCustomer) == 0) {
            throw ConflictException.with("Customer version does not match, customer was updated by another user");
        }
    }

    private int executeUpdate(final String sql, final Customer aCustomer) {
        final var aParams = new HashMap<String, Object>();
        aParams.put("id", aCustomer.getId().value());
        aParams.put("version", aCustomer.getVersion());
        aParams.put("idpUserId", aCustomer.getUserId().value());
        aParams.put(EMAIL_COLUMN, aCustomer.getEmail().value());
        aParams.put("firstName", aCustomer.getName().firstName());
        aParams.put("lastName", aCustomer.getName().lastName());
        aParams.put("documentNumber", aCustomer.getDocument().map(Document::value).orElse(null));
        aParams.put("documentType", aCustomer.getDocument().map(Document::type).orElse(null));
        aParams.put("phoneNumber", aCustomer.getTelephone().map(Telephone::value).orElse(null));
        aParams.put("createdAt", Timestamp.from(aCustomer.getCreatedAt()));
        aParams.put("updatedAt", Timestamp.from(aCustomer.getUpdatedAt()));

        return this.databaseClient.update(sql, aParams);
    }

    private RowMap<Customer> customerMapper() {
        return rs -> {
            final var aDocumentType = rs.getString("document_type");
            final var aPhoneNumber = rs.getString("phone_number");
            return Customer.with(
                    new CustomerId(UUID.fromString(rs.getString("id"))),
                    rs.getLong("version"),
                    new UserId(UUID.fromString(rs.getString("idp_user_id"))),
                    new Email(rs.getString(EMAIL_COLUMN)),
                    new Name(rs.getString("first_name"), rs.getString("last_name")),
                    aDocumentType != null ?
                            Document.create(rs.getString("document_number"), aDocumentType) : null,
                    aPhoneNumber != null ? new Telephone(aPhoneNumber) : null,
                    JdbcUtils.getInstant(rs, "created_at"),
                    JdbcUtils.getInstant(rs, "updated_at")
            );
        };
    }
}
