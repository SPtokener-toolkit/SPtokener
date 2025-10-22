public class ActionTokenNode extends ASTNode {
    String name;

    ActionTokenNode(String name) {
        this.name = name;
    }

    @Override
    String getMetaInfo() {
        return name;
    }
}
