package onlinebookstore;

import onlinebookstore.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    @Autowired BookRepository bookRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderCancelled_IncreaseStock(@Payload OrderCancelled orderCancelled){
        if(orderCancelled.validate()){
            System.out.println("##### listener cancelOrder IncreaseStock : " + orderCancelled.toJson());
            Book book = bookRepository.findByBookId(Long.valueOf(orderCancelled.getBookId()));
            book.setStock(book.getStock() + orderCancelled.getQty());
            bookRepository.save(book);
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){
        System.out.println();
        System.out.println();
        System.out.println("##### whatever test" );
        System.out.println();
        System.out.println();
    }
}
