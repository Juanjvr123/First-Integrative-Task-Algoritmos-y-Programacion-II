package model;

import structures.LinkedList;
import structures.Node;

import java.util.List;
import java.util.Scanner;

public class Chest {
    private LinkedList<Stack> stacks;
    public static final int MAX_STACKS = 50;
    private Season classificationType;
    private boolean isClassified;
    private String name;


    public Chest(String name) {
        this.stacks = new LinkedList<>();
        this.classificationType = Season.OTHER;
        this.isClassified = false;
        this.name = name;
    }

    public boolean addPlantation(Plantation plantation) {
        if (!classificationType.equals(Season.OTHER) && !classificationType.equals(plantation.getSeason())) {
            System.out.println("This chest only accept " + classificationType + "Plantations");
            return false;
        }


        Node<Stack> current = stacks.getHead();
        while (current != null) {
            Stack stack = current.getData();


            if (stack.getPlantation().getName().equalsIgnoreCase(plantation.getName())) {
                if (!stack.isFull()) {
                    boolean stackear = askUserToStack(plantation.getName());

                    if (stackear) {
                        stack.addPlantation(plantation);
                        return true;
                    }
                } else {
                    System.out.println("El stack está lleno. No se puede agregar más al stack.");
                }
            }
            current = current.getNextNode();
        }


        if (stacks.size() < MAX_STACKS) {
            Stack newStack = new Stack(plantation);
            stacks.add(newStack);
            System.out.println("Nueva plantación añadida en un nuevo stack.");
            return true;
        } else {
            System.out.println("Cofre lleno. No se puede agregar más stacks.");
            return false;
        }
    }

    public String getName() {
        return name;
    }

    private boolean askUserToStack(String plantationName) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ya existe un stack de " + plantationName + ". ¿Desea apilar esta plantación en el stack existente?");
        System.out.println("[1] Sí");
        System.out.println("[2] No, crear un nuevo stack");

        int option = scanner.nextInt();
        scanner.nextLine(); // Limpiar el buffer

        return option == 1; // Retorna true si elige apilar
    }

    private List<Stack> stacks() {
        return (List<Stack>) stacks;
    }

    public void organizeByName() {
        List<Stack> stackList = stacks();
        stackList.sort(new PlantationNameComparator());
        stacks = new LinkedList<>(stackList);
    }


    public void organizeBySeason() {
        List<Stack> stackList = stacks();
        stackList.sort(new PlantationSeasonComparator());
        stacks = new LinkedList<>();
    }

    public void organizeByGrowthDays() {
        List<Stack> stackList = stacks();
        stackList.sort(new PlantationGrowthDaysComparator());
        stacks = new LinkedList<>();
    }

    public Season getClassificationType() {
        return classificationType;
    }

    public void setClassificationType(Season classificationType) {
        this.classificationType = classificationType;
        this.isClassified = true;
    }

    public boolean isFull() {
        return stacks.size() >= MAX_STACKS;
    }

    public String listPlantations() {
        return stacks.printList();
    }



    public boolean isClassified() {
        return isClassified;
    }

    public LinkedList<Stack> getStacks() {
        return stacks;
    }


    public void addStack(Stack stack) {
        this.stacks.add(stack);
    }



    @Override
    public String toString() {
        return "Chest{" +
                "name='" + name + '\'' +
                ", classificationType='" + classificationType + '\'' +
                ", isClassified=" + isClassified +
                '}';
    }


}
