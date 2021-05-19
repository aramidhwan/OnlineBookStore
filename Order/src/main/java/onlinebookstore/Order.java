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

    @PostPersist
    public void onPostPersist(){
        //onlinebookstore.external.Book book = new onlinebookstore.external.Book();
        // Req/Res Calling
        boolean bResult = OrderApplication.applicationContext.getBean(onlinebookstore.external.BookService.class)
            .checkAndModifyStock(this.bookId, this.qty);

        if(bResult)
        {
            Ordered ordered = new Ordered();
            this.status="Ordered";
            BeanUtils.copyProperties(this, ordered);
            ordered.publishAfterCommit();
            System.out.println("PUB :: Ordered + orderId="+this.orderId);
        }
        else
        {
            OutOfStocked outOfStocked = new OutOfStocked();
            BeanUtils.copyProperties(this, outOfStocked);
            outOfStocked.setOrderDt(new Date());
            outOfStocked.publish();
            System.out.println("PUB :: OutOfStocked");
        }

    }

    @PreUpdate
    public void onPreUpdate(){
        System.out.println("status changed to " + this.status.toString());
        StatusChanged statusChanged = new StatusChanged();
        BeanUtils.copyProperties(this, statusChanged);
        statusChanged.publishAfterCommit();
    }

    @PreRemove
    public void onPreRemove(){
        OrderCancelled orderCancelled = new OrderCancelled();
        BeanUtils.copyProperties(this, orderCancelled);
        orderCancelled.publishAfterCommit();
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
