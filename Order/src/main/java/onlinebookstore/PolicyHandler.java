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
    @Autowired OrderRepository orderRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverDeliveryStarted_UpdateState(@Payload DeliveryStarted deliveryStarted){

        if(!deliveryStarted.validate()) return;

        System.out.println("\n\n##### listener UpdateState : " + deliveryStarted.toJson() + "\n\n");

        // Sample Logic //
        Order order = new Order();
        orderRepository.save(order);
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverDeliveryCancelled_UpdateState(@Payload DeliveryCancelled deliveryCancelled){

        if(!deliveryCancelled.validate()) return;

        System.out.println("\n\n##### listener UpdateState : " + deliveryCancelled.toJson() + "\n\n");

        // Sample Logic //
        Order order = new Order();
        orderRepository.save(order);
            
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
