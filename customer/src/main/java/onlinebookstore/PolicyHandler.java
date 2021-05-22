package onlinebookstore;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import onlinebookstore.config.kafka.KafkaProcessor;

@Service
public class PolicyHandler{
    @Autowired 
    CustomerRepository customerRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrdered_UpdateMileageAndGrade(@Payload Ordered ordered){

        if(!ordered.validate()) return;

        System.out.println("\n\n##### listener UpdateMileageAndGrade : " + ordered.toJson() + "\n\n");

        
        // ----- 로직 설명 (주문 받은 경우) -----
        // 주문되면 주문금액을 마일리지로 누적하고 누적 마일리지가 10000이상이 되면 
        // 등급을 SILVER->GOLD로 올린다. (신규 고객 등록 시 Default 등급은 SILVER 로 설정됨)

        Customer customer = new Customer();

        Optional<Customer> customerOptional = customerRepository.findById(ordered.getCustomerId());
        customer = customerOptional.get();
        customer.setMileage(customer.getMileage() + ordered.getPrice()); // 마일리지를 누적한다.

        if (customer.getMileage() >= 10000) { 
            customer.setGrade("GOLD");    // 마일리지가 10000 이상이면 GOLD 등급으로 올린다.
        }

        customerRepository.save(customer);
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderCancelled_UpdateMileageAndGrade(@Payload OrderCancelled orderCancelled){

        if(!orderCancelled.validate()) return;

        System.out.println("\n\n##### listener UpdateMileageAndGrade : " + orderCancelled.toJson() + "\n\n");

        
        // ----- 로직 설명 (주문 취소된 경우)-----
        // 주문이 취소되면 주문 취소금액 만큼 마일리지에서 삭감하고 누적 마일리지가 10000 미만이 되면 
        // 등급을 SILVER 로 내린다.

        Customer customer = new Customer();

        Optional<Customer> customerOptional = customerRepository.findById(orderCancelled.getCustomerId());
        customer = customerOptional.get();

        customer.setMileage(customer.getMileage() - orderCancelled.getPrice()); // 마일리지를 삭감한다.

        if (customer.getMileage() < 10000) { 
            customer.setGrade("SILVER");  // 마일리지가 10000 미만이면 SILVER 등급으로 내린다.
        }

        customerRepository.save(customer);
            
    }
            
    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}

}
