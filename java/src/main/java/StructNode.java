import java.util.Vector;

public class StructNode {
    Vector<StructNode> childs;
    StructNodeType type;
    StructNode pr;
    int[] token;
    Vector<String> identifiers;
    String mainIdentifier;

    StructNode(StructNodeType type) {
        token = new int[25];
        for (int i = 0; i < 25; ++i) {
            token[i] = 0;
        }
        this.type = type;
        childs = new Vector<>();
        identifiers = new Vector<>();
        pr = null;
        mainIdentifier = "";
    }
}
