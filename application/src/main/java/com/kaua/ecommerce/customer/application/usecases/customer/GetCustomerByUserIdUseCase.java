package com.kaua.ecommerce.customer.application.usecases.customer;

import com.kaua.ecommerce.customer.application.UseCase;
import com.kaua.ecommerce.customer.application.usecases.customer.outputs.GetCustomerByIdentifierOutput;
import com.kaua.ecommerce.customer.domain.customer.idp.UserId;

public abstract class GetCustomerByUserIdUseCase extends UseCase<UserId, GetCustomerByIdentifierOutput> {
}
