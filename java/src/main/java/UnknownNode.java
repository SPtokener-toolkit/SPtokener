public class UnknownNode extends ASTNode{
    String type;

    UnknownNode(String t) {
        super();
        type = t;
    }

    @Override
    String getMetaInfo() {
        return type;
    }

    @Override
    void setMetaInfo(String s) {
        type = s;
    }
}
