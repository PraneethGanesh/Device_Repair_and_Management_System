package com.example.vendor_service.Exception;

public class VendorNotFoundException extends RuntimeException{
    public VendorNotFoundException(String message) {
        super(message);
    }
}
