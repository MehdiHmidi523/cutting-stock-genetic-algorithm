public class Order {

    private int length;
    private int quantity;

    public Order(int p_length, int p_quantity) {
        if (p_length != 0 && p_quantity != 0) {
            setLength(p_length);
            setQuantity(p_quantity);
        }
    }

    public int getLength() {
        return length;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
