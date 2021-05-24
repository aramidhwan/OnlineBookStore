
package onlinebookstore;

import java.util.Date;

public class DeliveryStarted extends AbstractEvent {

    private Long deliveryid;
    private Long orderid;
    private String deliverystatus;
    private Date datetime;

    public Long getDeliveryid() {
        return deliveryid;
    }

    public void setDeliveryid(Long deliveryid) {
        this.deliveryid = deliveryid;
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

