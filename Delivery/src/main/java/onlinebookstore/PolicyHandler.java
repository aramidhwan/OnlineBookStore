package onlinebookstore;

import onlinebookstore.config.kafka.KafkaProcessor;

import java.util.Optional;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    @Autowired DeliveryRepository deliveryRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrdered_Delivery(@Payload Ordered ordered){

        if(!ordered.validate()) return;

        System.out.println("\n\n##### listener Delivery : " + ordered.toJson() + "\n\n");

        Delivery delivery = new Delivery();
        
        delivery.setOrderid(ordered.getOrderId());
        delivery.setDeliverystatus("Order-Delivery");         
        
        deliveryRepository.save(delivery);
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderCancelled_CancelDelivery(@Payload OrderCancelled orderCancelled){

        if(!orderCancelled.validate()) return;

        System.out.println("\n\n##### listener CancelDelivery : " + orderCancelled.toJson() + "\n\n");
        
        //Optional<Delivery> optionalDelivery = deliveryRepository.findByOrderid(orderCancelled.getOrderId());
        //Delivery delivery = optionalDelivery.get();        
        Delivery delivery = deliveryRepository.findByOrderid(orderCancelled.getOrderId());
        
        delivery.setDeliverystatus("Order Cancel-Delivery");        
    
        deliveryRepository.save(delivery);

    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
