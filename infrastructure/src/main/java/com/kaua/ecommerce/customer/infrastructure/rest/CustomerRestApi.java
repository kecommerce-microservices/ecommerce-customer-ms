package com.kaua.ecommerce.customer.infrastructure.rest;

import com.kaua.ecommerce.customer.application.usecases.customer.outputs.UpdateCustomerDocumentOutput;
import com.kaua.ecommerce.customer.application.usecases.customer.outputs.UpdateCustomerTelephoneOutput;
import com.kaua.ecommerce.customer.infrastructure.configurations.authentication.EcommerceUser;
import com.kaua.ecommerce.customer.infrastructure.rest.req.CreateCustomerRequest;
import com.kaua.ecommerce.customer.infrastructure.rest.req.UpdateCustomerDocumentRequest;
import com.kaua.ecommerce.customer.infrastructure.rest.req.UpdateCustomerTelephoneRequest;
import com.kaua.ecommerce.customer.infrastructure.rest.res.GetCustomerByIdentifierResponse;
import com.kaua.ecommerce.customer.infrastructure.rest.res.SignUpResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Customer", description = "Customer API")
@RequestMapping("/v1/customers")
public interface CustomerRestApi {

    @PostMapping(
            value = "/signup",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Create a new customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer created successfully"),
            @ApiResponse(responseCode = "400", description = "A validation error was observed"),
            @ApiResponse(responseCode = "422", description = "A business rule was violated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<SignUpResponse> signUp(@RequestBody CreateCustomerRequest request);

    @GetMapping(
            value = "/me",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Get the authenticated customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "400", description = "A input validation error was observed"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<GetCustomerByIdentifierResponse> getMe(@AuthenticationPrincipal final EcommerceUser principal);

    @GetMapping(
            value = "/users/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Get a customer by user id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "400", description = "A input validation error was observed"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<GetCustomerByIdentifierResponse> getByIdentifier(@PathVariable final String userId);

    @PatchMapping(
            value = "/document",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Update a customer document")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer document updated successfully"),
            @ApiResponse(responseCode = "400", description = "A validation error was observed"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "422", description = "A business rule was violated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<UpdateCustomerDocumentOutput> updateDocument(
            @AuthenticationPrincipal final EcommerceUser principal,
            @RequestBody UpdateCustomerDocumentRequest request
    );

    @PatchMapping(
            value = "/telephone",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Update a customer telephone")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer telephone updated successfully"),
            @ApiResponse(responseCode = "400", description = "A validation error was observed"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "422", description = "A business rule was violated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<UpdateCustomerTelephoneOutput> updateTelephone(
            @AuthenticationPrincipal final EcommerceUser principal,
            @RequestBody UpdateCustomerTelephoneRequest request
    );
}
