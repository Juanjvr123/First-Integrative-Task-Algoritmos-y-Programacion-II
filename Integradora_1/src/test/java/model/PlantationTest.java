package model;

import exceptions.ChestFullException;
import structures.Node;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlantationTest {

    private Plantation plantation;

    private void setUp1() {
        plantation = new Plantation("Ajo", Season.SPRING);
    }

    @Test
    public void validateInvalidGrowthDays() {
        setUp1();
        plantation.setGrowthDays(30);
        assertTrue(plantation.hasExceededSeason());
    }


}