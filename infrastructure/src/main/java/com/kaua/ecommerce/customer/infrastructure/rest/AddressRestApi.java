package com.kaua.ecommerce.customer.infrastructure.rest;

import com.kaua.ecommerce.customer.application.usecases.address.outputs.CreateCustomerAddressOutput;
import com.kaua.ecommerce.customer.application.usecases.address.outputs.UpdateAddressIsDefaultOutput;
import com.kaua.ecommerce.customer.application.usecases.address.outputs.UpdateAddressOutput;
import com.kaua.ecommerce.customer.infrastructure.configurations.authentication.EcommerceUser;
import com.kaua.ecommerce.customer.infrastructure.rest.req.address.CreateCustomerAddressRequest;
import com.kaua.ecommerce.customer.infrastructure.rest.req.address.UpdateAddressIsDefaultRequest;
import com.kaua.ecommerce.customer.infrastructure.rest.req.address.UpdateAddressRequest;
import com.kaua.ecommerce.customer.infrastructure.rest.res.GetAddressByIdResponse;
import com.kaua.ecommerce.customer.infrastructure.rest.res.GetDefaultAddressByCustomerIdResponse;
import com.kaua.ecommerce.customer.infrastructure.rest.res.ListCustomerAddressesResponse;
import com.kaua.ecommerce.lib.domain.pagination.Pagination;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping(
            value = "/{addressId}/default",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Update the default address for authenticated customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address successfully updated"),
            @ApiResponse(responseCode = "400", description = "A validation error was observed"),
            @ApiResponse(responseCode = "404", description = "Address was not found by identifier"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<UpdateAddressIsDefaultOutput> updateAddressIsDefault(
            @PathVariable String addressId,
            @RequestBody UpdateAddressIsDefaultRequest request
    );

    @PatchMapping(
            value = "/{addressId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Update the address for authenticated customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address successfully updated"),
            @ApiResponse(responseCode = "400", description = "A validation error was observed"),
            @ApiResponse(responseCode = "404", description = "Address was not found by identifier"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<UpdateAddressOutput> updateAddress(
            @PathVariable String addressId,
            @RequestBody UpdateAddressRequest request
    );

    @GetMapping(
            value = "/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Get the address by identifier for authenticated customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address successfully found"),
            @ApiResponse(responseCode = "404", description = "Address was not found by identifier"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<GetAddressByIdResponse> getAddressById(
            @PathVariable String addressId
    );

    @GetMapping(
            value = "/default",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Get the default address by customer identifier for authenticated customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address successfully found"),
            @ApiResponse(responseCode = "404", description = "Address was not found by customer identifier"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<GetDefaultAddressByCustomerIdResponse> getDefaultAddressByCustomerId(
            @AuthenticationPrincipal EcommerceUser user
    );

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Get all addresses by customer identifier for authenticated customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Addresses successfully found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    Pagination<ListCustomerAddressesResponse> listCustomerAddresses(
            @AuthenticationPrincipal EcommerceUser user,
            @RequestParam(name = "search", required = false, defaultValue = "") String search,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "perPage", required = false, defaultValue = "10") int perPage,
            @RequestParam(name = "sort", required = false, defaultValue = "title") String sort,
            @RequestParam(name = "direction", required = false, defaultValue = "asc") String direction
    );

    @DeleteMapping(
            value = "/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Delete the address by identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Address successfully deleted"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteAddressById(
            @PathVariable String addressId
    );
}
