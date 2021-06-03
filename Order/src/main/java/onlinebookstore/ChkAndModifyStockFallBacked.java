package onlinebookstore;

public class ChkAndModifyStockFallBacked extends AbstractEvent {
    private Long bookId;
    private Integer qty;

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
}
