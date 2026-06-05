package com.dms.customerservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class CompanyRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "GST number is required")
    private String gstNumber;

    private String address;

    // Constructors
    public CompanyRequest() {}

    public CompanyRequest(UUID userId, String companyName, String gstNumber, String address) {
        this.userId = userId;
        this.companyName = companyName;
        this.gstNumber = gstNumber;
        this.address = address;
    }

    // Getters
    public UUID getUserId() { return userId; }
    public String getCompanyName() { return companyName; }
    public String getGstNumber() { return gstNumber; }
    public String getAddress() { return address; }

    // Setters
    public void setUserId(UUID userId) { this.userId = userId; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }
    public void setAddress(String address) { this.address = address; }
}
