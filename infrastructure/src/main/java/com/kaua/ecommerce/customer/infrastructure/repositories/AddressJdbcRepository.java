package com.kaua.ecommerce.customer.infrastructure.repositories;

import com.kaua.ecommerce.customer.application.repositories.AddressRepository;
import com.kaua.ecommerce.customer.domain.address.Address;
import com.kaua.ecommerce.customer.domain.address.AddressId;
import com.kaua.ecommerce.customer.domain.address.Title;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.customer.infrastructure.jdbc.DatabaseClient;
import com.kaua.ecommerce.customer.infrastructure.jdbc.JdbcUtils;
import com.kaua.ecommerce.customer.infrastructure.jdbc.RowMap;
import com.kaua.ecommerce.lib.domain.pagination.Pagination;
import com.kaua.ecommerce.lib.domain.pagination.PaginationMetadata;
import com.kaua.ecommerce.lib.domain.pagination.SearchQuery;
import com.kaua.ecommerce.lib.infrastructure.exceptions.ConflictException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

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
        if (aAddress.getVersion() == 0) {
            log.debug("Creating a new address: {}", aAddress);
            create(aAddress);
            log.info("Address created: {}", aAddress);
        } else {
            log.debug("Updating address: {}", aAddress);
            update(aAddress);
            log.info("Address updated: {}", aAddress);
        }

        aAddress.incrementVersion();
        return aAddress;
    }

    @Override
    public Optional<Address> addressOfId(final AddressId aAddressId) {
        final var aSql = "SELECT * FROM addresses WHERE id = :id";
        return this.databaseClient.queryOne(aSql, Map.of("id", aAddressId.value()), addressMapper());
    }

    @Override
    public Optional<Address> addressByCustomerIdAndIsDefaultTrue(final CustomerId aCustomerId) {
        final var aSql = "SELECT * FROM addresses WHERE customer_id = :customerId AND is_default = true";
        return this.databaseClient.queryOne(aSql, Map.of(CUSTOMER_COLUMN, aCustomerId.value()), addressMapper());
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

    @Override
    public Pagination<Address> addressesByCustomerId(
            final CustomerId customerId,
            final SearchQuery searchQuery
    ) {
        final var aSqlRetrieve = """
                SELECT * FROM addresses
                WHERE customer_id = :customerId
                AND (:terms IS NULL OR LOWER(title) LIKE LOWER(:terms))
                ORDER BY
                CASE WHEN :sort IN ('title', 'created_at', 'updated_at') THEN :sort ELSE title END
                """ + Sort.Direction.fromString(searchQuery.direction()).name() + """
                 LIMIT :limit OFFSET (:offset)
                """;

        // Example: page = 1, perPage = 10, offset = 0 or page = 2, perPage = 10, offset = 10, offset speak to db where to start
        final var aOffset = Math.max(0, (searchQuery.page() - 1) * searchQuery.perPage());
        final var aTerms = StringUtils.isNotEmpty(searchQuery.terms())
                ? "%" + searchQuery.terms() + "%"
                : null;

        final Map<String, Object> aParams = new HashMap<>();
        aParams.put(CUSTOMER_COLUMN, customerId.value());
        aParams.put("terms", aTerms);
        aParams.put("sort", searchQuery.sort());
        aParams.put("limit", searchQuery.perPage());
        aParams.put("offset", aOffset);

        final var aItems = this.databaseClient.query(aSqlRetrieve, aParams, addressMapper());

        final var aTotalPages = (int) Math.ceil((double) aItems.size() / searchQuery.perPage());

        final var aMetadata = new PaginationMetadata(
                searchQuery.page(),
                searchQuery.perPage(),
                aTotalPages,
                aItems.size()
        );

        return new Pagination<>(aMetadata, aItems);
    }

    private void create(final Address aAddress) {
        final var aSql = """
                INSERT INTO addresses (id, version, title, customer_id, zip_code, number, street, city, district, country, state, complement, is_default, created_at, updated_at)
                VALUES (:id, (:version + 1), :title, :customerId, :zipCode, :number, :street, :city, :district, :country, :state, :complement, :isDefault, :createdAt, :updatedAt)
                """;
        executeUpdate(aSql, aAddress);
    }

    private void update(final Address aAddress) {
        final var aSql = """
                UPDATE addresses
                SET
                    version = :version + 1,
                    title = :title,
                    customer_id = :customerId,
                    zip_code = :zipCode,
                    number = :number,
                    street = :street,
                    city = :city,
                    district = :district,
                    country = :country,
                    state = :state,
                    complement = :complement,
                    is_default = :isDefault,
                    updated_at = :updatedAt
                WHERE id = :id AND version = :version
                """;

        if (executeUpdate(aSql, aAddress) == 0) {
            throw ConflictException.with("Address version does not match, address was updated by another user");
        }
    }

    private int executeUpdate(final String sql, final Address aAddress) {
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

        return this.databaseClient.update(sql, aParams);
    }

    private RowMap<Address> addressMapper() {
        return rs -> Address.with(
                new AddressId(UUID.fromString(rs.getString("id"))),
                rs.getLong("version"),
                new Title(rs.getString("title")),
                new CustomerId(UUID.fromString(rs.getString("customer_id"))),
                rs.getString("zip_code"),
                rs.getString("number"),
                rs.getString("street"),
                rs.getString("city"),
                rs.getString("district"),
                rs.getString("country"),
                rs.getString("state"),
                rs.getString("complement"),
                rs.getBoolean("is_default"),
                JdbcUtils.getInstant(rs, "created_at"),
                JdbcUtils.getInstant(rs, "updated_at")
        );
    }
}
