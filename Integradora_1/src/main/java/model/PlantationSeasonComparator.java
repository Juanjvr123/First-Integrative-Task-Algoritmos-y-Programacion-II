package model;

import java.util.Comparator;

public class PlantationSeasonComparator implements Comparator<Stack> {
    @Override
    public int compare(Stack s1, Stack s2) {
        return s1.getPlantation().getSeason().compareTo(s2.getPlantation().getSeason());
    }
}
