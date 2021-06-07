package onlinebookstore.external;

import org.springframework.stereotype.Component;
import feign.hystrix.FallbackFactory;
import onlinebookstore.ChkAndModifyStockFallBacked;

@Component
public class BookServiceFallbackFactory implements FallbackFactory<BookService> {

    @Override
    public BookService create(Throwable cause) { 
        return new BookService() {
            @Override
            public boolean chkAndModifyStock(Long bookId, int qty) {
                // HystrixTimeoutException 일 경우 Book 재고 회복
                if ( cause instanceof com.netflix.hystrix.exception.HystrixTimeoutException ) {
                    // kafka에 이벤트 발생(Book 재고 회복)
                    ChkAndModifyStockFallBacked chkAndModifyStockFallBacked = new ChkAndModifyStockFallBacked();
                    chkAndModifyStockFallBacked.setBookId(bookId);
                    chkAndModifyStockFallBacked.setQty(qty);
                    chkAndModifyStockFallBacked.publish();
                    System.out.println("####### Hystrix timeout occured ########");
                    System.out.println("** PUB :: ChkAndModifyStockFallBacked (by HystrixTimeoutException)");

                // Hystrix circuit OPEN 일 경우 Book 재고 회복 불필요
                } else {
                    System.out.println("####### BookServiceFallbacked kind ########");
                    System.out.println("####### " + cause.getMessage());
                }

                return false;
            }
        };
    }

}
