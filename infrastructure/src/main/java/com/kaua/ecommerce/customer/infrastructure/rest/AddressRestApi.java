package com.kaua.ecommerce.customer.infrastructure.rest;

import com.kaua.ecommerce.customer.application.usecases.address.outputs.CreateCustomerAddressOutput;
import com.kaua.ecommerce.customer.infrastructure.configurations.authentication.EcommerceUser;
import com.kaua.ecommerce.customer.infrastructure.rest.req.address.CreateCustomerAddressRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Address", description = "Address API")
@RequestMapping("/v1/addresses")
public interface AddressRestApi {

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Create a new address for authenticated customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Address successfully created"),
            @ApiResponse(responseCode = "400", description = "A validation error was observed"),
            @ApiResponse(responseCode = "422", description = "A business rule was violated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<CreateCustomerAddressOutput> createAddress(
            @AuthenticationPrincipal EcommerceUser user,
            @RequestBody CreateCustomerAddressRequest request
    );
}
