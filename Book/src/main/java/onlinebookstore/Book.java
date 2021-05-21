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
    @Transient
    private Integer stockBeforeUpdate;

    @PostPersist
    public void onPostPersist(){
        // 책 등록 
        BookRegistred bookRegistred = new BookRegistred();
        BeanUtils.copyProperties(this, bookRegistred);
        bookRegistred.publishAfterCommit();
    }


    @PostUpdate
    public void onPostUpdate(){

        if ( getStock() < getStockBeforeUpdate() ) {
            StockDecreased stockDecreased = new StockDecreased();
            BeanUtils.copyProperties(this, stockDecreased);
            stockDecreased.publishAfterCommit();

        } else if ( getStock() > getStockBeforeUpdate() ) {
            StockIncreased stockIncreased = new StockIncreased();
            BeanUtils.copyProperties(this, stockIncreased);
            stockIncreased.publishAfterCommit();
        } else {
            System.out.println("-----------------");
        }

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

    public Integer getStockBeforeUpdate() {
        return stockBeforeUpdate;
    }
    public void setStockBeforeUpdate(Integer stockBeforeUpdate) {
        this.stockBeforeUpdate = stockBeforeUpdate;
    }



}
