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
    @Autowired CustomerRepository customerRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrdered_UpdateMileageAndGrade(@Payload Ordered ordered){

        if(!ordered.validate()) return;

        System.out.println("\n\n##### listener UpdateMileageAndGrade : " + ordered.toJson() + "\n\n");

        // Sample Logic //
        Customer customer = new Customer();
        customerRepository.save(customer);
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderCancelled_UpdateMileageAndGrade(@Payload OrderCancelled orderCancelled){

        if(!orderCancelled.validate()) return;

        System.out.println("\n\n##### listener UpdateMileageAndGrade : " + orderCancelled.toJson() + "\n\n");

        // Sample Logic //
        Customer customer = new Customer();
        customerRepository.save(customer);
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderCancelled_UpdateMileageAndGrade(@Payload OrderCancelled orderCancelled){

        if(!orderCancelled.validate()) return;

        System.out.println("\n\n##### listener UpdateMileageAndGrade : " + orderCancelled.toJson() + "\n\n");

        // Sample Logic //
        Customer customer = new Customer();
        customerRepository.save(customer);
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderCancelled_UpdateMileageAndGrade(@Payload OrderCancelled orderCancelled){

        if(!orderCancelled.validate()) return;

        System.out.println("\n\n##### listener UpdateMileageAndGrade : " + orderCancelled.toJson() + "\n\n");

        // Sample Logic //
        Customer customer = new Customer();
        customerRepository.save(customer);
            
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
