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
        public static final String DOCUMENT_TYPE = "CPF";

        public Cpf {
            this.assertArgumentNotEmpty(value, "CPF", "should not be empty");
            this.assertConditionTrue(CpfUtils.validateCpf(value), "CPF", "should be valid");
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
        public static final String DOCUMENT_TYPE = "CNPJ";

        public Cnpj {
            this.assertArgumentNotEmpty(value, "CNPJ", "should not be empty");
            this.assertConditionTrue(CnpjUtils.validateCnpj(value), "CNPJ", "should be valid");
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
