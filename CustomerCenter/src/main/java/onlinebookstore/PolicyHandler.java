package onlinebookstore;

import onlinebookstore.config.kafka.KafkaProcessor;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
				System.out.println("#######################################################################");
	            System.out.println("##### Send SNS to Customer("+ marketingTarget.getEmail() +") that the new book [" + bookRegistred.getTitle() + "] is arrived.");
	            System.out.println("#######################################################################");
			}
        });

    }
    
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverStockIncreased_NoticeReStock(@Payload StockIncreased stockIncreased){

        if (!stockIncreased.validate()) return;
		
		Set<Long> alreadySendedCustomerId = new HashSet<>() ;
        List<OutOfStockOrder> list = outOfStockOrderRepository.findByBookId(stockIncreased.getBookId()) ;
        
		for (OutOfStockOrder outOfStockOrder:list) {
			
			// 이미 SNS 발송된 고객은 Skip
			if (alreadySendedCustomerId.contains(outOfStockOrder.getCustomerId())) {
				continue;
			}

        	Optional<MarketingTarget> optional = marketingTargetRepository.findByCustomerId(outOfStockOrder.getCustomerId()) ;
			
        	if (optional.isPresent()) {
		
        		MarketingTarget marketingTarget = optional.get();
	            System.out.println("#######################################################################");
	            System.out.println("##### Send SNS to Customer("+ marketingTarget.getEmail() +") that the book [" + stockIncreased.getTitle() + "] is restocked.");
	            System.out.println("#######################################################################");

				alreadySendedCustomerId.add(marketingTarget.getCustomerId()) ;
        	}
        }
    }
}
