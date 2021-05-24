package onlinebookstore;

import javax.persistence.*;  

@Entity
@Table(name="MarketingTarget_table")
public class MarketingTarget {

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
