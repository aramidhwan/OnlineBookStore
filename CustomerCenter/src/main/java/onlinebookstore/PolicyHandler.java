package onlinebookstore;

import onlinebookstore.config.kafka.KafkaProcessor;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
	@Autowired
	CustomerRepository customerRepository ;
	@Autowired
	OutOfStockOrderRepository outOfStockOrderRepository ;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverBookRegistred_NoticeNewBook(@Payload BookRegistred bookRegistred){

        if(!bookRegistred.validate()) return;
        
        Iterable<Customer> iterable = customerRepository.findAll();
        
        // Send SNS with iterable HERE.
        iterable.forEach(new Consumer<Customer>() {
			@Override
			public void accept(Customer customer) {
	            System.out.println("\n##### Send SNS to Customer("+ customer.getEmail() +") that the new book [" + bookRegistred.getTitle() + "] is arrived.");
			}
        });

    }
    
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverStockIncreased_NoticeReStock(@Payload StockIncreased stockIncreased){

        if (!stockIncreased.validate()) return;
        
        List<OutOfStockOrder> list = outOfStockOrderRepository.findByBookId(stockIncreased.getBookId()) ;
        
        for (OutOfStockOrder outOfStockOrder:list) {
        	Optional<Customer> optional = customerRepository.findByCustomerId(outOfStockOrder.getCustomerId()) ;
        	
        	if (optional.isPresent()) {
        		Customer customer = optional.get();
            	System.out.println("\n##### Send SNS to Customer("+ customer.getEmail() +") that the book [" + stockIncreased.getTitle() + "] is restocked.");
        	}
        }
    }
}
