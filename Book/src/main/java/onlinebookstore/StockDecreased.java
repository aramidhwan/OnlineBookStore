package onlinebookstore;

public class StockDecreased extends AbstractEvent {

    private Long bookId;
    private Integer stock;

    public StockDecreased(){
        super();
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
