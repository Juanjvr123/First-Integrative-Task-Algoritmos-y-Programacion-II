package controller;

import exceptions.*;
import model.Chest;
import model.Plantation;
import model.Season;
import model.Stack;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class ControllerTest {
    private Controller controller;
    private Chest chest;

    private void setUp1() {
        controller = new Controller();
    }

    private void setUp2(){
        controller = new Controller();
        controller.setCurrentSeason(Season.SPRING);
    }

    private void setUp3() throws IncorrectSeasonException, InventoryFullException {
        controller = new Controller();
        controller.setCurrentSeason(Season.SUMMER);
        controller.createPlantationByName("Amapola");
        controller.movePlantationFromCropFieldToInventory("Amapola");
    }

    private void setUp4() throws IncorrectSeasonException {
        controller = new Controller();
        controller.setCurrentSeason(Season.WINTER);
        Plantation plantation = new Plantation("Melon de Polvo", Season.WINTER);
        for (int i = 0; i < controller.inventory.size(); i++) {
            controller.inventory.add(plantation);
        }
        controller.setCurrentSeason(Season.AUTUMN);
        controller.createPlantationByName("Fruto Qi");
    }

    private void setUp5() {
        controller = new Controller();
        chest = new Chest("Cofre Lleno");

        for (int i = 0; i < 10; i++) {
            Plantation plantation = new Plantation("Plantation" + i, Season.SUMMER);
            chest.addPlantation(plantation);
        }
    }

    private void setUp6() {
        controller = new Controller();

        chest = new Chest("Cofre de Prueba");

        Stack stack1 = new Stack(new Plantation("Aguacate", Season.SUMMER));
        //Stack stack2 = new Stack(new Plantation("Fruto Qi", Season.SUMMER));
        //Stack stack3 = new Stack(new Plantation("Zanahoria", Season.SUMMER));

        chest.addStack(stack1);
        //chest.addStack(stack2);
        //chest.addStack(stack3);
    }

    @Test
    public void testCreatePlantationInCorrectSeason() throws IncorrectSeasonException {

        setUp2();

        boolean result = controller.createPlantationByName("Ajo");

        assertTrue(result, "La plantación debería añadirse correctamente cuando la estación es la adecuada.");
    }

    @Test
    public void testAddPlantationInIncorrectSeason() {
        setUp2();

        IncorrectSeasonException exception = assertThrows(IncorrectSeasonException.class, () -> {
            controller.createPlantationByName("Berenjena");
        });

        assertEquals("Cannot plant Berenjena in the current season: SPRING", exception.getMessage());
    }

    @Test
    public void testHarvestInexistentPlantation() throws InventoryFullException {
        setUp1();

        assertFalse(controller.movePlantationFromCropFieldToInventory("Arandano"));

    }

    @Test
    public void testHarvestPlantationWithFullInventory() throws InventoryFullException, IncorrectSeasonException {
        setUp4();

        assertThrows(InventoryFullException.class, () -> {
            controller.movePlantationFromCropFieldToInventory("Fruto Qi");
        });
    }

    @Test
    public void testCreateChestSuccessfully() throws InexistentChestException, DuplicatedChestException {
        setUp6();
        boolean chestCreated = controller.createChest("Cofre de Prueba");

        assertTrue(chestCreated, "El cofre debería haberse creado correctamente.");

        Chest chest = controller.searchChestByName("Cofre de Madera");
        assertNotNull(chest, "El cofre debería estar en la lista.");
        assertEquals("Cofre de Madera", chest.getName(), "El nombre del cofre debería coincidir.");
    }

    @Test
    public void testClassifyCreatedChest() throws InexistentChestException, DuplicatedChestException {
        setUp1();
        controller.createChest("Cofre de Clasificación");
        Chest chest = controller.searchChestByName("Cofre de Clasificación");

        assertNotNull(chest, "El cofre debería haberse creado.");

        controller.classifyChest( "Cofre de Clasificación", 2);
        assertEquals("SUMMER", chest.getClassificationType(), "El cofre debería estar clasificado como 'SUMMER'.");
    }

    @Test
    public void testCreateChestWithExistingNameThrowsException() {
        setUp1();
        assertDoesNotThrow(() -> controller.createChest("Cofre de Madera"));

        InexistentChestException exception = assertThrows(InexistentChestException.class, () -> {
            controller.createChest("Cofre de Madera");
        });

        assertEquals("El cofre con el nombre 'Cofre de Madera' ya existe.", exception.getMessage(), "El mensaje de error debería ser correcto.");
    }

    @Test
    public void testChestFull() throws ChestFullException {
        setUp5();

        ChestFullException exception = assertThrows(ChestFullException.class, () -> {
            chest.addPlantation(new Plantation("Tomate", Season.SUMMER));
        });

        assertEquals("El cofre está lleno", exception.getMessage(), "Debería lanzarse una excepción indicando que el cofre está lleno.");
        assertTrue(chest.isFull(), "El método isFull debería devolver true si el cofre está lleno.");
    }

    @Test
    public void listChestTest(){
        setUp6();

        assertEquals("Cofre de Prueba", controller.listChests());

    }

    @Test
    public void searchChestTest() throws InexistentChestException {
        setUp6();
        assertEquals(controller.searchChestByName("Cofre de Prueba"), chest);
    }

    @Test
    public void searchInexistentChestTest() throws InexistentChestException {
        setUp6();
        assertThrows(InexistentChestException.class, () -> {controller.searchChestByName("Cofre De Plantas");});
    }

    @Test
    public void listPlantationsInChestTest() throws InexistentChestException {
        setUp6();

        assertEquals(controller.listChestPlantations(chest), "Aguacate");
    }
    @Test
    public void getPlantationFromInventoryTest() throws InexistentPlantationException, IncorrectSeasonException, InventoryFullException {
        setUp3();
        Plantation plantation = new Plantation("Amapola", Season.SUMMER);
        assertEquals(controller.getPlantationFromInventory("Amapola"), plantation);
        assertThrows(InexistentPlantationException.class, () -> {controller.getPlantationFromInventory("Baya de gema dulce");});
    }

    @Test
    public void organizeChestByGrowthDaysTest() {
        setUp6();
        assertTrue(controller.organizeChestByGrowthDays());
    }
}
