
package onlinebookstore.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name="Book", url="http://localhost:8082")
public interface BookService {

    @RequestMapping(method= RequestMethod.GET, path="/books/chkAndModifyStock")
    public boolean chkAndModifyStock(@RequestParam("bookId") Long bookId,
                                        @RequestParam("qty") int qty);

}