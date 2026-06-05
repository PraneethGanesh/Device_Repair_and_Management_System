package com.example.customer_service.dto;

import jakarta.validation.constraints.NotBlank;

public class CompanyRequest {

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "GST number is required")
    private String gstNumber;

    private String address;

    // Constructors
    public CompanyRequest() {}

    public CompanyRequest(String companyName, String gstNumber, String address) {
        this.companyName = companyName;
        this.gstNumber = gstNumber;
        this.address = address;
    }

    // Getters
    public String getCompanyName() { return companyName; }
    public String getGstNumber() { return gstNumber; }
    public String getAddress() { return address; }

    // Setters
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }
    public void setAddress(String address) { this.address = address; }
}
