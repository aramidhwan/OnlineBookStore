package onlinebookstore;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Book_table")
public class Book {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long bookId;
    private String title;
    private Integer stock;

    @PostPersist
    public void onPostPersist(){
        // 책 등록 
        BookRegistred bookRegistred = new BookRegistred();
        BeanUtils.copyProperties(this, bookRegistred);
        bookRegistred.publishAfterCommit();
    }


    @PostUpdate
    public void onPostUpdate(){
        // 책 수정(제목,재고,상태..)
        BookModified bookModified = new BookModified();
        BeanUtils.copyProperties(this, bookModified);
        bookModified.publishAfterCommit();


    }


    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }




}
