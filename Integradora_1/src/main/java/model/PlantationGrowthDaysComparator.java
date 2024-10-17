package model;

import java.util.Comparator;

public class PlantationGrowthDaysComparator implements Comparator<Stack> {
    @Override
    public int compare(Stack s1, Stack s2) {
        return Integer.compare(s1.getPlantation().getGrowthDays(), s2.getPlantation().getGrowthDays());
    }
}
