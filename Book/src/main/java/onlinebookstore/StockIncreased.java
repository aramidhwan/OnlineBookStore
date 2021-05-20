
package onlinebookstore;

public class StockIncreased extends AbstractEvent {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

