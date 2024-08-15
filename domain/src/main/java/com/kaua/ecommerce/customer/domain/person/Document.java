package com.kaua.ecommerce.customer.domain.person;

import com.kaua.ecommerce.lib.domain.ValueObject;
import com.kaua.ecommerce.lib.domain.utils.CnpjUtils;
import com.kaua.ecommerce.lib.domain.utils.CpfUtils;

public sealed interface Document extends ValueObject {

    String value();

    String formattedValue();

    String type();

    static Document create(final String documentNumber, final String documentType) {
        return DocumentFactory.create(documentNumber, documentType);
    }

    record Cpf(String value) implements Document {
        public static final String DOCUMENT_TYPE = "cpf";

        public Cpf {
            this.assertArgumentNotEmpty(value, "cpf", "should not be empty");
            this.assertConditionTrue(CpfUtils.validateCpf(value), "cpf", "should be valid");
        }

        @Override
        public String formattedValue() {
            return CpfUtils.formatCpf(value);
        }

        @Override
        public String type() {
            return DOCUMENT_TYPE;
        }
    }

    record Cnpj(String value) implements Document {
        public static final String DOCUMENT_TYPE = "cnpj";

        public Cnpj {
            this.assertArgumentNotEmpty(value, "cnpj", "should not be empty");
            this.assertConditionTrue(CnpjUtils.validateCnpj(value), "cnpj", "should be valid");
        }

        @Override
        public String formattedValue() {
            return CnpjUtils.formatCnpj(value);
        }

        @Override
        public String type() {
            return DOCUMENT_TYPE;
        }
    }
}
