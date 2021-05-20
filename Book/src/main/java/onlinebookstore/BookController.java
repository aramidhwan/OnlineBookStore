package onlinebookstore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Optional;

 @RestController
 public class BookController {
     @Autowired  BookRepository bookRepository;

     @RequestMapping(value = "/books/chkAndModifyStock",
             method = RequestMethod.GET,
             produces = "application/json;charset=UTF-8")
     public boolean chkAndModifyStock(@RequestParam("bookId") Long bookId,
                                      @RequestParam("qty")  int qty)
             throws Exception {
         System.out.println("##### /books/chkAndModifyStock  called #####");
         boolean status = false;
         Optional<Book> bookOptional = bookRepository.findByBookId(bookId);
         Book book = bookOptional.get();
         // 현 재고보다 주문수량이 적거나 같은경우에만 true 회신
         if( book.getStock() >= qty){
             status = true;
             book.setStockBeforeUpdate(book.getStock());
             book.setStock(book.getStock() - qty); // 주문수량만큼 재고 감소
             bookRepository.save(book);
      }

      return status;
  }


     @RequestMapping(value = "/books/reStock",
             method = RequestMethod.PATCH,
             produces = "application/json;charset=UTF-8")
     @Transactional
     public void reStock(@RequestBody Book book)
             throws Exception {
         long bookId = book.getBookId();
         int stock = book.getStock();

         Optional<Book> bookOptional = bookRepository.findByBookId(bookId);

         if (bookOptional.isPresent()) {
             Book originalBook = bookOptional.get();
             originalBook.setStockBeforeUpdate(originalBook.getStock());
             originalBook.setStock(originalBook.getStock() + stock);
             bookRepository.save(originalBook);
         }
     }
 }
