package onlinebookstore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

 @RestController
 public class BookController {
 @Autowired  BookRepository bookRepository;

  @RequestMapping(value = "/books/chkAndModifyStock",
          method = RequestMethod.GET,
          produces = "application/json;charset=UTF-8")
  public boolean chkAndModifyStock(HttpServletRequest request, HttpServletResponse response) throws Exception {
   boolean status = false;
   Long bookId = Long.valueOf(request.getParameter("bookId"));
   int qty = Integer.parseInt(request.getParameter("qty"));

   Book book = bookRepository.findByBookId(bookId);
   // 현 재고보다 주문수량이 적거나 같은경우에만 true 회신
   if( book.getStock() >= qty){
    status = true;
    book.setStock(book.getStock() - qty); // 주문수량만큼 재고 감소
    bookRepository.save(book);
   }

   System.out.println("##### /books/chkAndModifyStock  called #####");
   return status;
  }

 }
