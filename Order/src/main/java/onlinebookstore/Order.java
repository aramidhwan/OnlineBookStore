package onlinebookstore;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.Date;

@Entity
@Table(name="Order_table")
public class Order {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long orderId;
    private Long bookId;
    private Integer qty;
    private Integer price;
    private Integer paymentId;
    private Long customerId;
    private Date orderDt;
    private String status;

    @PrePersist
    public void onPrePersist(){
        //onlinebookstore.external.Book book = new onlinebookstore.external.Book();
        // Req/Res Calling
        boolean bResult = OrderApplication.applicationContext.getBean(onlinebookstore.external.BookService.class)
            .chkAndModifyStock(this.bookId, this.qty);

        this.price = qty * 10000;
        this.orderDt = new Date();
        this.paymentId = 100000001;
                
        if(bResult)
        {
            this.status="Ordered";
        }
        else
        {
            this.status="OutOfStocked";
        }

    }

    @PostPersist
    public void onPostPersist(){
        if(this.status.equals("Ordered"))
        {
            Ordered ordered = new Ordered();
            BeanUtils.copyProperties(this, ordered);
            ordered.publishAfterCommit();
            System.out.println("** PUB :: Ordered : orderId="+this.orderId);
        }
        else
        {
            OutOfStocked outOfStocked = new OutOfStocked();
            BeanUtils.copyProperties(this, outOfStocked);
            outOfStocked.publish();
            System.out.println("** PUB :: OutOfStocked : orderId="+this.orderId);
        }
    }

    @PreUpdate
    public void onPreUpdate(){
        if(this.status.equals("Order Cancelled"))
        {
            System.out.println("** PUB :: OrderCancelled : orderId" + this.orderId);
            OrderCancelled orderCancelled = new OrderCancelled();
            BeanUtils.copyProperties(this, orderCancelled);
            orderCancelled.publishAfterCommit();
        }
        else {
            System.out.println("** PUB :: StatusChanged : status changed to " + this.status.toString());
            StatusChanged statusChanged = new StatusChanged();
            BeanUtils.copyProperties(this, statusChanged);
            statusChanged.publishAfterCommit();
        }
        
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    public Date getOrderDt() {
        return orderDt;
    }

    public void setOrderDt(Date orderDt) {
        this.orderDt = orderDt;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }




}
