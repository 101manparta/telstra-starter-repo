package au.com.telstra.simcardactivator;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
public class SimCardActivationController {

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/activate")
    public ResponseEntity<String> activateSim(@RequestBody ActivationRequest request) {
        System.out.println("=== ACTIVATION REQUEST RECEIVED ===");
        System.out.println("ICCID: " + request.getIccid());
        System.out.println("Email: " + request.getCustomerEmail());

        try {
            // 1. Prepare request to actuator
            String actuatorUrl = "http://localhost:8444/actuate";
            ActuatorRequest actuatorRequest = new ActuatorRequest(request.getIccid());

            // 2. Call actuator service
            System.out.println("Calling actuator at: " + actuatorUrl);
            ActuatorResponse actuatorResponse = restTemplate.postForObject(
                    actuatorUrl,
                    actuatorRequest,
                    ActuatorResponse.class
            );

            // 3. Process response
            System.out.println("Actuator response: " + actuatorResponse);

            if (actuatorResponse != null && actuatorResponse.isSuccess()) {
                String successMessage = String.format(
                        "SIM card with ICCID %s activated successfully for customer %s",
                        request.getIccid(),
                        request.getCustomerEmail()
                );
                return ResponseEntity.ok(successMessage);
            } else {
                return ResponseEntity.badRequest().body("Activation failed: Actuator returned failure");
            }

        } catch (Exception e) {
            System.err.println("Error calling actuator: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body("Error activating SIM card: " + e.getMessage());
        }
    }

    // DTO for incoming request
    public static class ActivationRequest {
        private String iccid;
        private String customerEmail;

        // Getters and setters
        public String getIccid() { return iccid; }
        public void setIccid(String iccid) { this.iccid = iccid; }
        public String getCustomerEmail() { return customerEmail; }
        public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    }

    // DTO for actuator request
    public static class ActuatorRequest {
        private String iccid;

        public ActuatorRequest(String iccid) {
            this.iccid = iccid;
        }

        public String getIccid() { return iccid; }
        public void setIccid(String iccid) { this.iccid = iccid; }
    }

    // DTO for actuator response
    public static class ActuatorResponse {
        private boolean success;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        @Override
        public String toString() {
            return "ActuatorResponse{success=" + success + "}";
        }
    }
}