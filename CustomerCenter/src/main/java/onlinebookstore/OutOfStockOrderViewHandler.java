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
public class OutOfStockOrderViewHandler {


    @Autowired
    private OutOfStockOrderRepository outOfStockOrderRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenOutOfStocked_then_CREATE_1 (@Payload OutOfStocked outOfStocked) {
        try {

            if (!outOfStocked.validate()) return;

            // view 객체 생성
            OutOfStockOrder outOfStockOrder = new OutOfStockOrder();
            // view 객체에 이벤트의 Value 를 set 함
            outOfStockOrder.setOrderId(outOfStocked.getId());
            outOfStockOrder.setBookId(outOfStocked.getBookId());
            outOfStockOrder.setCustomerId(outOfStocked.getCustomerId());
            outOfStockOrder.setOrderDt(outOfStocked.getOrderDt());
            // view 레파지 토리에 save
            outOfStockOrderRepository.save(outOfStockOrder);
        
        }catch (Exception e){
            e.printStackTrace();
        }
    }



}