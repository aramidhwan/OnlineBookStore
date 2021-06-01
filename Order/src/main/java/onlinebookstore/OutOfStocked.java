package onlinebookstore;

import java.util.Date;

public class OutOfStocked extends AbstractEvent {

    private Long orderId;
    private Long bookId;
    private Long customerId;
    private String status;
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private Date orderDt;

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
}