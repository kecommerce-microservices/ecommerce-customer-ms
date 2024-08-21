package com.kaua.ecommerce.customer.application;

public abstract class UnitUseCase<I> {

    public abstract void execute(I input);
}
