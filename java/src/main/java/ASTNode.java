import java.util.Vector;

public class ASTNode {
    Vector<ASTNode> children;
    ASTNode parent;

    ASTNode() {
        children = new Vector<>();
    }

    String getMetaInfo() {
        return "";
    }

    void setMetaInfo(String s) {}
}