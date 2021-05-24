package onlinebookstore;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Delivery_table")
public class Delivery {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long deliveryId;
    private Long orderid;
    private String deliverystatus;
    private Date datetime;

    @PostPersist
    public void onPostPersist(){
        // DeliveryStarted deliveryStarted = new DeliveryStarted();
        // BeanUtils.copyProperties(this, deliveryStarted);
        // deliveryStarted.setOrderid(this.orderid);
        // System.out.println("Orderid : " + deliveryStarted.getOrderid());
        // deliveryStarted.setDeliveryid((long) (Math.random()%100));
        // System.out.println("Deliveryid : " + deliveryStarted.getDeliveryid());                
        // deliveryStarted.setDeliverystatus("Delivery Start");
        // deliveryStarted.publishAfterCommit();

        if (this.deliverystatus == "Order-Delivery") {    
            DeliveryStarted deliveryStarted = new DeliveryStarted();
            BeanUtils.copyProperties(this, deliveryStarted);
            deliveryStarted.setOrderid(this.orderid);
            System.out.println("Orderid : " + deliveryStarted.getOrderid());
            deliveryStarted.setDeliveryid((long) (Math.random()%100));
            System.out.println("Deliveryid : " + deliveryStarted.getDeliveryid());                
            deliveryStarted.setDeliverystatus("Delivery Start");
            deliveryStarted.publishAfterCommit();
        }
        // else if (this.deliverystatus == "Order Cancel-Delivery") {
        else{
            DeliveryCancelled deliveryCancelled = new DeliveryCancelled();
            BeanUtils.copyProperties(this, deliveryCancelled);
            deliveryCancelled.setOrderid(this.orderid);        
            deliveryCancelled.setDeliverystatus("Delivery Cancel");
            deliveryCancelled.publishAfterCommit();
        }
    }

    // @PrePersist
    // public void onPrePersist(){
    //     DeliveryCancelled deliveryCancelled = new DeliveryCancelled();
    //     BeanUtils.copyProperties(this, deliveryCancelled);
    //     deliveryCancelled.setOrderid(this.orderid);        
    //     deliveryCancelled.setDeliverystatus("Delivery Cancel");
    //     deliveryCancelled.publishAfterCommit();
    // }


    public Long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Long deliveryId) {
        this.deliveryId = deliveryId;
    }
    public Long getOrderid() {
        return orderid;
    }

    public void setOrderid(Long orderid) {
        this.orderid = orderid;
    }
    public String getDeliverystatus() {
        return deliverystatus;
    }

    public void setDeliverystatus(String deliverystatus) {
        this.deliverystatus = deliverystatus;
    }
    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }




}
