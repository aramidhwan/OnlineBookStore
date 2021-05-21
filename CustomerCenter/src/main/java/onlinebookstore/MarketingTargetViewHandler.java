package onlinebookstore;

import onlinebookstore.config.kafka.KafkaProcessor;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class MarketingTargetViewHandler {


    @Autowired
    private MarketingTargetRepository marketingTargetRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenCustermerRegistered_then_CREATE_1 (@Payload CustermerRegistered custermerRegistered) {
        try {

            if (!custermerRegistered.validate()) return;

            // view 객체 생성
            MarketingTarget customer = new MarketingTarget();
            // view 객체에 이벤트의 Value 를 set 함
            customer.setCustomerId(custermerRegistered.getId());
            customer.setEmail(custermerRegistered.getEmail());
            // view 레파지 토리에 save
            marketingTargetRepository.save(customer);
        
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenCustomerModified_then_UPDATE_1(@Payload CustomerModified customerModified) {
        try {
            if (!customerModified.validate()) return;
                // view 객체 조회
            Optional<MarketingTarget> optional = marketingTargetRepository.findByCustomerId(customerModified.getId());
            if( optional.isPresent()) {
            	MarketingTarget marketingTarget = optional.get();
                // view 객체에 이벤트의 eventDirectValue 를 set 함
            	marketingTarget.setEmail(customerModified.getEmail());
                // view 레파지 토리에 save
                marketingTargetRepository.save(marketingTarget);
            }
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}