package onlinebookstore;

import onlinebookstore.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyPageViewHandler {


    @Autowired
    private MyPageRepository myPageRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrdered_then_CREATE_1 (@Payload Ordered ordered) {
        try {

            if (!ordered.validate()) return;

            // view 객체 생성
            MyPage myPage = new MyPage();
            // view 객체에 이벤트의 Value 를 set 함
            myPage.setCustomerId(ordered.getCustomerId());
            myPage.setOrderId(ordered.getOrderId());
            myPage.setOrderStatus(ordered.getStatus());
            // view 레파지토리에 save
            myPageRepository.save(myPage);
        
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrdered_then_CREATE_2 (@Payload OutOfStocked outOfStocked) {
        try {

            if (!outOfStocked.validate()) return;

            // view 객체 생성
            MyPage myPage = new MyPage();
            // view 객체에 이벤트의 Value 를 set 함
            myPage.setCustomerId(outOfStocked.getCustomerId());
            myPage.setOrderId(outOfStocked.getOrderId());
            myPage.setOrderStatus(outOfStocked.getStatus());
            // view 레파지토리에 save
            myPageRepository.save(myPage);
        
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrderCancelled_then_UPDATE_1(@Payload OrderCancelled orderCancelled) {
        try {
            if (!orderCancelled.validate()) return;
            
            // view 객체 조회
            List<MyPage> myPageList = myPageRepository.findByOrderId(orderCancelled.getOrderId());
            
            for(MyPage myPage : myPageList){
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                myPage.setOrderStatus(orderCancelled.getStatus());
                // view 레파지 토리에 save
                myPageRepository.save(myPage);
            }
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenStatusChanged_then_UPDATE_2(@Payload StatusChanged statusChanged) {
        try {
            if (!statusChanged.validate()) return;
            
            // view 객체 조회
            List<MyPage> myPageList = myPageRepository.findByOrderId(statusChanged.getOrderId());
            for(MyPage myPage : myPageList){
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                myPage.setOrderStatus(statusChanged.getStatus());
                // view 레파지 토리에 save
                myPageRepository.save(myPage);
            }
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}