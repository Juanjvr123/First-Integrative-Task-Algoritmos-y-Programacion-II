package exceptions;

public class InventoryEmptyException extends Exception {
    public InventoryEmptyException(String message) {
        super(message);
    }
}
