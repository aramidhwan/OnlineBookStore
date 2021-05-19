package onlinebookstore;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="MyPage_table")
public class MyPage {

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;
        private Long customerId;
        private Long orderId;
        private String orderStatus;


        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
        public Long getCustomerId() {
            return customerId;
        }

        public void setCustomerId(Long customerId) {
            this.customerId = customerId;
        }
        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }
        public String getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(String orderStatus) {
            this.orderStatus = orderStatus;
        }

}
