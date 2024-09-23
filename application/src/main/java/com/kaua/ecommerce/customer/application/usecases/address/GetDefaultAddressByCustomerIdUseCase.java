package com.kaua.ecommerce.customer.application.usecases.address;

import com.kaua.ecommerce.customer.application.UseCase;
import com.kaua.ecommerce.customer.application.usecases.address.outputs.GetDefaultAddressByCustomerIdOutput;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;

public abstract class GetDefaultAddressByCustomerIdUseCase extends
        UseCase<CustomerId, GetDefaultAddressByCustomerIdOutput> {
}
