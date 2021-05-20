package onlinebookstore;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MyPageRepository extends CrudRepository<MyPage, Long> {

    List<MyPage> findByOrderId(Long orderId);

}