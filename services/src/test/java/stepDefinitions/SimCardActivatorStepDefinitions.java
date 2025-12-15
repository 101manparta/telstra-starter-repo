package stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration
public class SimCardActivatorStepDefinitions {
    
    @LocalServerPort
    private int port;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private String iccid;
    private String customerEmail;
    private ResponseEntity<String> activationResponse;
    private ResponseEntity<QueryResponse> queryResponse;
    private static int activationCounter = 0;
    
    public static class ActivationRequest {
        private String iccid;
        private String customerEmail;
        
        public ActivationRequest() {}
        
        public ActivationRequest(String iccid, String customerEmail) {
            this.iccid = iccid;
            this.customerEmail = customerEmail;
        }
        
        public String getIccid() { return iccid; }
        public void setIccid(String iccid) { this.iccid = iccid; }
        
        public String getCustomerEmail() { return customerEmail; }
        public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    }
    
    public static class QueryResponse {
        private String iccid;
        private String customerEmail;
        private boolean active;
        
        public String getIccid() { return iccid; }
        public void setIccid(String iccid) { this.iccid = iccid; }
        
        public String getCustomerEmail() { return customerEmail; }
        public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
        
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
    }
    
    @Given("I have a valid SIM card with ICCID {string}")
    public void i_have_a_valid_sim_card_with_iccid(String iccid) {
        this.iccid = iccid;
    }
    
    @Given("I have an invalid SIM card with ICCID {string}")
    public void i_have_an_invalid_sim_card_with_iccid(String iccid) {
        this.iccid = iccid;
    }
    
    @Given("I have customer email {string}")
    public void i_have_customer_email(String email) {
        this.customerEmail = email;
    }
    
    @When("I submit an activation request")
    public void i_submit_an_activation_request() {
        activationCounter++;
        
        String url = "http://localhost:" + port + "/api/activate";
        ActivationRequest request = new ActivationRequest(iccid, customerEmail);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<ActivationRequest> entity = new HttpEntity<>(request, headers);
        activationResponse = restTemplate.postForEntity(url, entity, String.class);
        
        System.out.println("Activation Response: " + activationResponse.getBody());
    }
    
    @Then("the activation should be successful")
    public void the_activation_should_be_successful() {
        assertEquals(HttpStatus.OK, activationResponse.getStatusCode());
        assertTrue(activationResponse.getBody().contains("successfully"));
    }
    
    @Then("the activation should fail")
    public void the_activation_should_fail() {
        assertEquals(HttpStatus.BAD_REQUEST, activationResponse.getStatusCode());
        assertTrue(activationResponse.getBody().contains("failed"));
    }
    
    @And("the database should show the SIM card as active")
    public void the_database_should_show_the_sim_card_as_active() {
        verifyDatabaseRecord(true);
    }
    
    @And("the database should show the SIM card as inactive")
    public void the_database_should_show_the_sim_card_as_inactive() {
        verifyDatabaseRecord(false);
    }
    
    private void verifyDatabaseRecord(boolean expectedActive) {
        int expectedId = activationCounter;
        
        String url = "http://localhost:" + port + "/api/query?simCardId=" + expectedId;
        queryResponse = restTemplate.getForEntity(url, QueryResponse.class);
        
        assertEquals(HttpStatus.OK, queryResponse.getStatusCode());
        assertNotNull(queryResponse.getBody());
        
        QueryResponse response = queryResponse.getBody();
        assertEquals(iccid, response.getIccid());
        assertEquals(customerEmail, response.getCustomerEmail());
        assertEquals(expectedActive, response.isActive());
        
        System.out.println("Database verification - ID: " + expectedId + 
                          ", ICCID: " + response.getIccid() + 
                          ", Active: " + response.isActive());
    }
}
