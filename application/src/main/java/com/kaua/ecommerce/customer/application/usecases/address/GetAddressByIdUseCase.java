package com.kaua.ecommerce.customer.application.usecases.address;

import com.kaua.ecommerce.customer.application.UseCase;
import com.kaua.ecommerce.customer.application.usecases.address.outputs.GetAddressByIdOutput;
import com.kaua.ecommerce.customer.domain.address.AddressId;

public abstract class GetAddressByIdUseCase extends UseCase<AddressId, GetAddressByIdOutput> {
}
