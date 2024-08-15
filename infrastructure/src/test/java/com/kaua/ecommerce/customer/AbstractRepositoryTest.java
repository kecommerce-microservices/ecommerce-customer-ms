package com.kaua.ecommerce.customer;

import com.kaua.ecommerce.customer.infrastructure.jdbc.JdbcClientAdapter;
import com.kaua.ecommerce.customer.infrastructure.repositories.CustomerJdbcRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.jdbc.JdbcTestUtils;

@DataJdbcTest
@Tag("integrationTest")
@ActiveProfiles("test-integration")
public abstract class AbstractRepositoryTest {

    private static final String CUSTOMERS_TABLE = "customers";

    @Autowired
    private JdbcClient jdbcClient;

    private CustomerJdbcRepository customerJdbcRepository;

    @BeforeEach
    void setUp() {
        this.customerJdbcRepository = new CustomerJdbcRepository(new JdbcClientAdapter(jdbcClient));
    }

    protected int countCustomers() {
        return JdbcTestUtils.countRowsInTable(jdbcClient, CUSTOMERS_TABLE);
    }

    public CustomerJdbcRepository customerRepository() {
        return customerJdbcRepository;
    }
}
