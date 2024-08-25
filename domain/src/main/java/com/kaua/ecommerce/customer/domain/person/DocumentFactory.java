package com.kaua.ecommerce.customer.domain.person;

import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.utils.CnpjUtils;
import com.kaua.ecommerce.lib.domain.utils.CpfUtils;

public final class DocumentFactory {

    private DocumentFactory() {}

    public static Document create(final String documentNumber, final String documentType) {
        return switch (documentType.toUpperCase()) {
            case Document.Cpf.DOCUMENT_TYPE -> new Document.Cpf(CpfUtils.cleanCpf(documentNumber));
            case Document.Cnpj.DOCUMENT_TYPE -> new Document.Cnpj(CnpjUtils.cleanCnpj(documentNumber));
            default -> throw DomainException.with("Invalid document type");
        };
    }
}
