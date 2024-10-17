package structures;

import model.Plantation;
import model.Season;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NodeTest {

    private Node node;

    private void setUp1() {
        Plantation plantation = new Plantation("Berenjena", Season.SPRING);
        node = new Node(plantation);
    }

    @Test
    public void validateNullAfterLast() {
        setUp1();
        assertNull(node.getNextNode());
    }


}
