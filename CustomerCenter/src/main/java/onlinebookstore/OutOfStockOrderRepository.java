package onlinebookstore;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OutOfStockOrderRepository extends CrudRepository<OutOfStockOrder, Long> {


}