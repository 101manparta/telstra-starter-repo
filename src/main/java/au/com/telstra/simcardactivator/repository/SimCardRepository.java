package au.com.telstra.simcardactivator.repository;

import au.com.telstra.simcardactivator.entity.SimCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SimCardRepository extends JpaRepository<SimCard, Long> {
    Optional<SimCard> findByIccid(String iccid);
    Optional<SimCard> findByCustomerEmail(String customerEmail);
}
