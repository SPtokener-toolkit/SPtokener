public class InnerNode extends ASTNode {
    NodeType type;
    int startLine, endLine;

    InnerNode(NodeType t, int startLine, int endLine) {
        super();
        type = t;
        this.startLine = startLine;
        this.endLine = endLine;
    }
}
