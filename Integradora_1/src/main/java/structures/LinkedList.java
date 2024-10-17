package structures;

import model.Plantation;
import model.Stack;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class LinkedList<T> implements Iterable<T> {
    private Node<T> head;
    private Node<T> tail;

    public LinkedList() {
        head = null;
        tail = null;
    }

    public LinkedList(List<Stack> stackList) {
    }

    public void setHead(Node<T> head) {
        this.head = head;
    }

    public Node<T> getTail() {
        return tail;
    }

    public void setTail(Node<T> tail) {
        this.tail = tail;
    }

    public void add(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.setNextNode(newNode);
            tail = newNode;
        }
    }

    public String printList() {
        StringBuilder sb = new StringBuilder();

        if (isEmpty()) {
            sb.append("The list is empty");
        } else {
            Node<T> current = head;
            while (current != null) {
                sb.append(current.getData().toString()).append("\n");
                current = current.getNextNode();
            }
        }

        return sb.toString();
    }

    public void removeFirst() {
        if (head != null) {
            head = head.getNextNode();
            if (head == null) {
                tail = null;  // La lista está vacía después de eliminar el primer nodo
            }
        }
    }
    public boolean remove(T data) {
        if (isEmpty()) {
            return false;
        }

        if (head.getData().equals(data)) {
            removeFirst();
            return true;
        }

        Node<T> current = head;
        Node<T> prev = null;

        while (current != null) {
            if (current.getData().equals(data)) {
                if (prev != null) {
                    prev.setNextNode(current.getNextNode());
                    if (current == tail) {
                        tail = prev;
                    }
                }
                return true;
            }
            prev = current;
            current = current.getNextNode();
        }
        return false;
    }
    public boolean isEmpty() {
        return head == null;
    }

    public Node<T> getHead() {
        return head;
    }

    public int size() {
        int count = 0;
        Node<T> current = head;
        while (current != null) {
            count++;
            current = current.getNextNode();
        }
        return count;
    }

    public T find(T data) {
        Node<T> current = head;
        while (current != null) {
            if (current.getData().equals(data)) {
                return current.getData();
            }
            current = current.getNextNode();
        }
        return null;
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListIterator(head);
    }

    // Clase interna para el iterador
    private class LinkedListIterator implements Iterator<T> {
        private Node<T> current;

        public LinkedListIterator(Node<T> startNode) {
            this.current = startNode;
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() {
            T data = current.getData();
            current = current.getNextNode();
            return data;
        }
    }

    public T searchElement(Predicate<T> criteria) {
        Node<T> current = head;

        while (current != null) {
            if (criteria.test(current.getData())) {
                return current.getData();
            }
            current = current.getNextNode();
        }

        return null;
    }



}

