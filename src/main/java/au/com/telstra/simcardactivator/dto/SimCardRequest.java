package au.com.telstra.simcardactivator.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SimCardRequest {
    private final String iccid;
    private final String customerEmail;
    
    @JsonCreator
    public SimCardRequest(@JsonProperty("iccid") String iccid, 
                         @JsonProperty("customerEmail") String customerEmail) {
        this.iccid = iccid;
        this.customerEmail = customerEmail;
    }
    
    public String getIccid() { return iccid; }
    public String getCustomerEmail() { return customerEmail; }
}