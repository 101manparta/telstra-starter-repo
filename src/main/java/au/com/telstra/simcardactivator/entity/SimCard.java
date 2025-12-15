package au.com.telstra.simcardactivator.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sim_card")
public class SimCard {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String iccid;
    
    @Column(name = "customer_email", nullable = false)
    private String customerEmail;
    
    @Column(name = "active", nullable = false)
    private boolean active;
    
    @Column(name = "activation_time")
    private LocalDateTime activationTime;
    
    public SimCard() {
        this.activationTime = LocalDateTime.now();
    }
    
    public SimCard(String iccid, String customerEmail, boolean active) {
        this.iccid = iccid;
        this.customerEmail = customerEmail;
        this.active = active;
        this.activationTime = LocalDateTime.now();
    }
    
    public Long getId() { return id; }
    
    public String getIccid() { return iccid; }
    public void setIccid(String iccid) { this.iccid = iccid; }
    
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public LocalDateTime getActivationTime() { return activationTime; }
    public void setActivationTime(LocalDateTime activationTime) { this.activationTime = activationTime; }
    
    @Override
    public String toString() {
        return "SimCard{id=" + id + ", iccid='" + iccid + "', customerEmail='" + customerEmail + "', active=" + active + "}";
    }
}
