
package onlinebookstore.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name="Book", url="http://Book:8080")
public interface BookService {

    @RequestMapping(method= RequestMethod.GET, path="/books/chkAndModifyStock")
    public boolean checkAndModifyStock(@RequestParam("bookId") Long bookId,
                                        @RequestParam("qty") int qty);

}