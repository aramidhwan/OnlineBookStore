package onlinebookstore;

import onlinebookstore.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerViewHandler {


    @Autowired
    private CustomerRepository customerRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenCustermerRegistered_then_CREATE_1 (@Payload CustermerRegistered custermerRegistered) {
        try {

            if (!custermerRegistered.validate()) return;

            // view 객체 생성
            Customer customer = new Customer();
            // view 객체에 이벤트의 Value 를 set 함
            customer.setCustomerId(custermerRegistered.getId());
            customer.setEmail(custermerRegistered.getEmail());
            // view 레파지 토리에 save
            customerRepository.save(customer);
        
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenCustomerModified_then_UPDATE_1(@Payload CustomerModified customerModified) {
        try {
            if (!customerModified.validate()) return;
                // view 객체 조회
            Optional<Customer> customerOptional = customerRepository.findByCustomerId(customerModified.getId());
            if( customerOptional.isPresent()) {
                Customer customer = customerOptional.get();
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                    customer.setEmail(customerModified.getEmail());
                // view 레파지 토리에 save
                customerRepository.save(customer);
            }
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}