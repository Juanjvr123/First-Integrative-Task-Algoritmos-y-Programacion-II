package model;

import java.util.Comparator;

public class PlantationNameComparator implements Comparator<Stack> {
    @Override
    public int compare(Stack s1, Stack s2) {
        return s1.getPlantation().getName().compareToIgnoreCase(s2.getPlantation().getName());
    }
}
