package au.com.telstra.simcardactivator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import au.com.telstra.simcardactivator.dto.SimCardRequest;
import au.com.telstra.simcardactivator.entity.SimCard;
import au.com.telstra.simcardactivator.repository.SimCardRepository;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class SimCardActivationController {

    private final SimCardRepository simCardRepository;
    private final RestTemplate restTemplate;
    private static final String ACTIVATOR_URL = "http://localhost:8081/activate";

    @Autowired
    public SimCardActivationController(SimCardRepository simCardRepository) {
        this.simCardRepository = simCardRepository;
        this.restTemplate = new RestTemplate();
    }

    @PostMapping("/activate")
    public ResponseEntity<String> activateSimCard(@RequestBody SimCardRequest request) {
        try {
            // Send activation request to external service
            ResponseEntity<ActivationResult> response = restTemplate.postForEntity(
                ACTIVATOR_URL,
                request,
                ActivationResult.class
            );

            ActivationResult result = response.getBody();
            if (result == null) {
                // Save failed attempt to database
                SimCard simCard = new SimCard(
                    request.getIccid(),
                    request.getCustomerEmail(),
                    false
                );
                simCardRepository.save(simCard);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Activation service returned empty response");
            }
            
            boolean success = result.isSuccess();
            
            // Save to database
            SimCard simCard = new SimCard(
                request.getIccid(),
                request.getCustomerEmail(),
                success
            );
            simCardRepository.save(simCard);

            HttpStatus status = success ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
            String message = success ? "SIM card activated successfully!" : "SIM card activation failed!";
            
            return ResponseEntity.status(status).body(message);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/query")
    public ResponseEntity<SimCard> querySimCard(@RequestParam Long simCardId) {
        Optional<SimCard> simCardOptional = simCardRepository.findById(simCardId);
        
        return simCardOptional
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @GetMapping("/queryByIccid")
    public ResponseEntity<SimCard> querySimCardByIccid(@RequestParam String iccid) {
        Optional<SimCard> simCardOptional = simCardRepository.findByIccid(iccid);
        
        return simCardOptional
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ActivationResult {
        private boolean success;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }
    }
}