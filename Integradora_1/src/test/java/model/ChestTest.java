package model;

import controller.Controller;
import exceptions.ChestFullException;
import exceptions.InexistentChestException;
import structures.Node;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ChestTest {

    private Controller controller;
    private Chest chest;

    private void setUp1() {
        chest = new Chest("Cofre de Prueba");

        Stack stack1 = new Stack(new Plantation("Aguacate", Season.SUMMER));
        Stack stack2 = new Stack(new Plantation("Fruto Qi", Season.SUMMER));
        Stack stack3 = new Stack(new Plantation("Zanahoria", Season.SUMMER));

        chest.addStack(stack1);
        chest.addStack(stack2);
        chest.addStack(stack3);
    }

    private void setUp2() {
        chest = new Chest("Cofre de Clasificación");
        chest.setClassificationType(Season.WINTER);
    }

    @Test
    public void getChestClassificationTest(){
        setUp1();
        assertEquals(chest.getClassificationType(), Season.WINTER);
    }

}
