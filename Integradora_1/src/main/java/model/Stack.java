package model;

import structures.LinkedList;
import structures.Node;

public class Stack {
    private Plantation plantation; // El tipo de plantación que está en el stack
    private int count; // Cantidad de items en el stack
    private LinkedList<Plantation> plantations;
    private static final int MAX_COUNT = 25; // Capacidad máxima de un stack

    public Stack(Plantation plantation) {
        this.plantation = plantation;
        this.count = 1; // Iniciar el stack con un item
        this.plantations = new LinkedList<>();
        this.plantations.add(plantation);
    }

    public boolean addPlantation(Plantation plantation) {
        if (!isFull()) {
            plantations.add(plantation);
            count++;
            System.out.println("Plantación añadida al stack.");
            return true;
        } else {
            System.out.println("El stack está lleno.");
            return false;
        }
    }



    public boolean removeItem() {
        if (count > 0) {
            count--;
            return true;
        }
        return false;
    }

    public Plantation getPlantation() {
        return plantation;
    }

    public boolean isFull() {
        return count >= MAX_COUNT;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public LinkedList<Plantation> getPlantations() {
        return plantations;
    }

    public void removePlantation(Plantation plantation) {
        Node<Plantation> current = plantations.getHead();
        Node<Plantation> previous = null;

        while (current != null) {
            if (current.getData().equals(plantation)) {

                if (previous == null) {
                    plantations.removeFirst(); // Si es la primera del stack
                } else {
                    previous.setNextNode(current.getNextNode());
                }
                break;
            }
            previous = current;
            current = current.getNextNode();
        }
    }

    @Override
    public String toString() {
        return plantation.getName() + " x " + count;
    }
}
