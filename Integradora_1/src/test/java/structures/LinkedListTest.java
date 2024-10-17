package structures;

import model.Plantation;
import model.Season;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LinkedListTest {

    private LinkedList<Plantation> plantations;

    public void setUp1() {
        plantations = new LinkedList<>();
    }

    @Test
    public void testAddElementToList() {
        setUp1();
        Plantation plantation = new Plantation("Tomate", Season.SUMMER);
        plantations.add(plantation);

        assertEquals(1, plantations.size());

        assertEquals("Tomate", plantations.getHead().getData().getName());
    }

    @Test
    public void testRemoveElementFromList() {
        setUp1();
        Plantation plantation1 = new Plantation("Chirivia", Season.SUMMER);
        Plantation plantation2 = new Plantation("Ajo", Season.SPRING);
        plantations.add(plantation1);
        plantations.add(plantation2);

        plantations.remove(plantation1);

        assertEquals("Ajo", plantations.getHead().getData().getName());
    }

    @Test
    public void testListIsEmptyInitially() {
        setUp1();
        assertTrue(plantations.isEmpty());
    }
}
