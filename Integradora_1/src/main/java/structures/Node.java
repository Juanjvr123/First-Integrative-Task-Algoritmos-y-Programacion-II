package structures;

public class Node <T>{
    private T Data;
    private Node <T>NextNode;



    public Node(T data) {
        Data = data;
    }

    public T getData() {
        return Data;
    }

    public Node<T> getNextNode() {
        return NextNode;
    }

    public void setData(T data) {
        Data = data;
    }

    public void setNextNode(Node<T> nextNode) {
        NextNode = nextNode;
    }
}

