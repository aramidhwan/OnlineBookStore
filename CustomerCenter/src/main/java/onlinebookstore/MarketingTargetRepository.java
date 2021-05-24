package onlinebookstore;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MarketingTargetRepository extends CrudRepository<MarketingTarget, Long> {

	Optional<MarketingTarget> findByCustomerId(Long customerId);

}