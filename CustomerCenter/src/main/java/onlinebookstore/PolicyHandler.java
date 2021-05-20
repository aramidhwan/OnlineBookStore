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
	MarketingTargetRepository marketingTargetRepository ;
	@Autowired
	OutOfStockOrderRepository outOfStockOrderRepository ;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverBookRegistred_NoticeNewBook(@Payload BookRegistred bookRegistred){

        if(!bookRegistred.validate()) return;
        
        Iterable<MarketingTarget> iterable = marketingTargetRepository.findAll();
        
        // Send SNS with iterable HERE.
        iterable.forEach(new Consumer<MarketingTarget>() {
			@Override
			public void accept(MarketingTarget marketingTarget) {
	            System.out.println("\n##### Send SNS to Customer("+ marketingTarget.getEmail() +") that the new book [" + bookRegistred.getTitle() + "] is arrived.");
			}
        });

    }
    
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverStockIncreased_NoticeReStock(@Payload StockIncreased stockIncreased){

        if (!stockIncreased.validate()) return;
        
        List<OutOfStockOrder> list = outOfStockOrderRepository.findByBookId(stockIncreased.getBookId()) ;
        
        for (OutOfStockOrder outOfStockOrder:list) {
        	Optional<MarketingTarget> optional = marketingTargetRepository.findByCustomerId(outOfStockOrder.getCustomerId()) ;
        	
        	if (optional.isPresent()) {
        		MarketingTarget marketingTarget = optional.get();
            	System.out.println("\n##### Send SNS to Customer("+ marketingTarget.getEmail() +") that the book [" + stockIncreased.getTitle() + "] is restocked.");
        	}
        }
    }
}
