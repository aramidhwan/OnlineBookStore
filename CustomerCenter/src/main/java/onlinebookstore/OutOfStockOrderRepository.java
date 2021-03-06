package onlinebookstore;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OutOfStockOrderRepository extends CrudRepository<OutOfStockOrder, Long> {

	List<OutOfStockOrder> findByBookId(Long long1);
}