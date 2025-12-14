package au.com.telstra.simcardactivator;

public class ActuatorResponse {
    private boolean success;
    
    // Default constructor
    public ActuatorResponse() {}
    
    // Parameterized constructor
    public ActuatorResponse(boolean success) {
        this.success = success;
    }
    
    // Getter and Setter
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    @Override
    public String toString() {
        return "ActuatorResponse{" +
                "success=" + success +
                '}';
    }
}
