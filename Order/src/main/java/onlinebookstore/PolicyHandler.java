package onlinebookstore;

import onlinebookstore.config.kafka.KafkaProcessor;
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

        System.out.println("SUB :: DeliveryStarted-UpdateState deliveryId="+ deliveryStarted.getDeliveryid() + ", orderId=" + deliveryStarted.getOrderid());

        Order order = orderRepository.findById(deliveryStarted.getOrderid()).get();
        order.setStatus("DeliveryStarted");
        orderRepository.save(order);
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverDeliveryCancelled_UpdateState(@Payload DeliveryCancelled deliveryCancelled){

        if(!deliveryCancelled.validate()) return;

        System.out.println("SUB :: DeliveryCancelled-UpdateState deliveryId="+ deliveryCancelled.getDeliveryid() + ", orderId=" + deliveryCancelled.getOrderid());

        Order order = orderRepository.findById(deliveryCancelled.getOrderid()).get();
        order.setStatus("DeliveryCancelled");
        orderRepository.save(order);

    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
