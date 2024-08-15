package com.kaua.ecommerce.customer.application;

public abstract class UseCase<I, O> {

    public abstract O execute(I input);
}
