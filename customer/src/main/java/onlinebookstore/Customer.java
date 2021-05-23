package onlinebookstore;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Customer_table")
public class Customer {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long customerId;
    private String name;
    private String grade = "SILVER";  // 신규 고객 등록 시 Defalut 등급은 SILVER로 설정됨
    private Integer mileage = 0;
    private String email;

    @PostPersist
    public void onPostPersist(){
        CustermerRegistered custermerRegistered = new CustermerRegistered();
        BeanUtils.copyProperties(this, custermerRegistered);
        custermerRegistered.publishAfterCommit();


        // CustomerModified customerModified = new CustomerModified();
        // BeanUtils.copyProperties(this, customerModified);
        // customerModified.publishAfterCommit();





    }

    @PostUpdate
    public void onPostUpdate(){
        CustomerModified customerModified = new CustomerModified();
        BeanUtils.copyProperties(this, customerModified);
        customerModified.publishAfterCommit();

        MileageAndGradeUpdated mileageAndGradeUpdated = new MileageAndGradeUpdated();
        BeanUtils.copyProperties(this, mileageAndGradeUpdated);
        mileageAndGradeUpdated.publishAfterCommit();
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}