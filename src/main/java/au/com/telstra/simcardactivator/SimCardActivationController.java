package au.com.telstra.simcardactivator;

import au.com.telstra.simcardactivator.entity.SimCard;
import au.com.telstra.simcardactivator.repository.SimCardRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class SimCardActivationController {
    
    private final RestTemplate restTemplate;
    private final SimCardRepository simCardRepository;
    
    public SimCardActivationController(RestTemplate restTemplate, SimCardRepository simCardRepository) {
        this.restTemplate = restTemplate;
        this.simCardRepository = simCardRepository;
    }
    
    @PostMapping("/activate")
    public ResponseEntity<String> activateSimCard(@RequestBody ActivationRequest request) {
        System.out.println("=== ACTIVATION REQUEST RECEIVED ===");
        System.out.println("ICCID: " + request.getIccid());
        System.out.println("Email: " + request.getCustomerEmail());
        
        try {
            String actuatorUrl = "http://localhost:8444/actuate";
            ActuatorRequest actuatorRequest = new ActuatorRequest(request.getIccid());
            
            System.out.println("Calling actuator at: " + actuatorUrl);
            ActuatorResponse actuatorResponse = restTemplate.postForObject(actuatorUrl, actuatorRequest, ActuatorResponse.class);
            
            System.out.println("Actuator response: " + actuatorResponse);
            
            boolean activationSuccess = actuatorResponse != null && actuatorResponse.isSuccess();
            
            SimCard simCard = new SimCard(request.getIccid(), request.getCustomerEmail(), activationSuccess);
            SimCard savedSimCard = simCardRepository.save(simCard);
            System.out.println("Saved to database! ID: " + savedSimCard.getId());
            
            if (activationSuccess) {
                String successMessage = "SIM card activated successfully! Database ID: " + savedSimCard.getId();
                return ResponseEntity.ok(successMessage);
            } else {
                return ResponseEntity.badRequest().body("SIM card activation failed");
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
    
    @GetMapping("/query")
    public ResponseEntity<?> querySimCard(@RequestParam("simCardId") Long simCardId) {
        System.out.println("=== QUERY REQUEST ===");
        System.out.println("Requested ID: " + simCardId);
        
        Optional<SimCard> simCardOptional = simCardRepository.findById(simCardId);
        
        if (simCardOptional.isPresent()) {
            SimCard simCard = simCardOptional.get();
            QueryResponse response = new QueryResponse();
            response.setIccid(simCard.getIccid());
            response.setCustomerEmail(simCard.getCustomerEmail());
            response.setActive(simCard.isActive());
            
            System.out.println("Found: " + simCard);
            return ResponseEntity.ok(response);
        } else {
            System.out.println("Not found for ID: " + simCardId);
            return ResponseEntity.notFound().build();
        }
    }
    
    public static class ActivationRequest {
        private String iccid;
        private String customerEmail;
        
        public String getIccid() { return iccid; }
        public void setIccid(String iccid) { this.iccid = iccid; }
        
        public String getCustomerEmail() { return customerEmail; }
        public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    }
    
    public static class ActuatorRequest {
        private String iccid;
        
        public ActuatorRequest(String iccid) { this.iccid = iccid; }
        public String getIccid() { return iccid; }
        public void setIccid(String iccid) { this.iccid = iccid; }
    }
    
    public static class ActuatorResponse {
        private boolean success;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        @Override
        public String toString() { return "ActuatorResponse{success=" + success + "}"; }
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
}
