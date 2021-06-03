package onlinebookstore;

import onlinebookstore.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PolicyHandler{
    @Autowired BookRepository bookRepository;

    @StreamListener(KafkaProcessor.INPUT)
    @Transactional
    public void wheneverOrderCancelled_IncreaseStock(@Payload OrderCancelled orderCancelled){
        if(orderCancelled.validate()){
            System.out.println("##### listener cancelOrder IncreaseStock : " + orderCancelled.toJson());

            Optional<Book> bookOptional = bookRepository.findByBookId(orderCancelled.getBookId());
            if ( bookOptional.isPresent()) {
                Book book = bookOptional.get();
                book.setStockBeforeUpdate(book.getStock());
                book.setStock(book.getStock() + orderCancelled.getQty());

                bookRepository.save(book);
            }
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    @Transactional
    public void wheneverChkAndModifyStockFallBacked_IncreaseStock(@Payload ChkAndModifyStockFallBacked chkAndModifyStockFallBacked){
        if(chkAndModifyStockFallBacked.validate()){
            Optional<Book> bookOptional = bookRepository.findByBookId(chkAndModifyStockFallBacked.getBookId());
            if ( bookOptional.isPresent()) {
                Book book = bookOptional.get();
                book.setStockBeforeUpdate(book.getStock());
                book.setStock(book.getStock() + chkAndModifyStockFallBacked.getQty());

                bookRepository.save(book);
            }
        }
    }

}