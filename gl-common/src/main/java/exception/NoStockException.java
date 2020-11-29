package exception;

public class NoStockException extends RuntimeException {
    public NoStockException(String skuId) {
        super(skuId+" has no stock");
    }
}
