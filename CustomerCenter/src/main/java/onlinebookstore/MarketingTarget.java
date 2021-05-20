package onlinebookstore;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="Customer_table")
public class Customer {

        @Id
        private Long customerId;
        private String email;


        public Long getCustomerId() {
            return customerId;
        }

        public void setCustomerId(Long customerId) {
            this.customerId = customerId;
        }
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

}
