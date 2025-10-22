import java.util.Arrays;
import java.util.HashSet;
import java.util.Vector;

public class TokenBuilder {

    int n; // variable for n-grams
    Vector<MethodTokens> methods;
    HashSet<String> banned;

    TokenBuilder(int n) {
        this.n = n;
        methods = new Vector<>();
        banned = new HashSet<>();
    }

    void buildTokens(String path, String lang) {
        String[] parts = path.split("/");
        if (banned.contains(parts[parts.length - 1])) {
            return;
        }
        ASTBuilder astb = new ASTBuilder();
        ASTNode root;
        try {
            root = astb.buildAsts(path, lang);
        }
        catch (Exception e) {
            System.err.println(e);
            return;
        }
        if (root == null) {
            return;
        }
        parseMethods(root, path);
        for (MethodTokens method : methods) {
            method.computeSemanticTokens();
        }
    }

    void parseMethods(ASTNode root, String path) {
        if (root instanceof InnerNode) {
            if (((InnerNode)root).type == NodeType.METHOD_DEF) {
                int[] tokenArr = new int[25];
                for (int i = 0; i < 25; ++i) {
                    tokenArr[i] = 0;
                }
                StructNode rootStruct = new StructNode(StructNodeType.UNDEFINED);
                MethodTokens meth = new MethodTokens(path, ((InnerNode)root).startLine, ((InnerNode)root).endLine, rootStruct, n);
                HashSet<String> methodActionTokens = new HashSet<>();
                int tokens = parseMethod(root, tokenArr, rootStruct, methodActionTokens);
                meth.root = rootStruct;
                meth.tokensCnt = tokens;
                meth.actionTokens = methodActionTokens;
                methods.add(meth);
            }
        }
        for (ASTNode ch : root.children) {
            parseMethods(ch, path);
        }
    }

    int parseMethod(ASTNode root, int[] tokenArr, StructNode structNode, HashSet<String> actionTokens) {
        int tokens = 0;
        if (root instanceof IdentifierNode) {
            structNode.type = StructNodeType.IDENTIFIER;
            structNode.mainIdentifier = ((IdentifierNode)root).name;
            tokens += 1;
        }
        if (root instanceof InnerNode) {
            InnerNode node = (InnerNode)root;
            if (node.type == NodeType.METHOD_INVOC) {
                structNode.type = StructNodeType.METHOD;
                getIdentifiers(root, structNode.identifiers);
                if (node.children.get(0) instanceof IdentifierNode) {
                    structNode.mainIdentifier = ((IdentifierNode)node.children.get(0)).name;
                }
                else {
                    structNode.mainIdentifier = "hard";
                }
            }
            else if (node.type == NodeType.LOGICAL_EXPR || node.type == NodeType.NUMERIC_EXPR || node.type == NodeType.CONDITION_EXPR) {
                structNode.type = StructNodeType.OPERATION;
                getIdentifiers(root, structNode.identifiers);
            }
            else if (node.type == NodeType.ASSIGN_EXPR) {
                Vector<String> v = new Vector<>();
                if (root.children.get(0) instanceof IdentifierNode) {
                    getIdentifiers(root.children.get(0), v);
                    String name = v.get(0);
                    structNode.mainIdentifier = name;
                    getIdentifiers(root.children.get(1), structNode.identifiers);   
                }
            }
            else if (node.type == NodeType.VAR_DECL || node.type == NodeType.ARRAY_SELECTOR) {
                getIdentifiers(root, structNode.identifiers);
                if (structNode.identifiers.size() > 0) {
                    structNode.mainIdentifier = structNode.identifiers.get(0);
                    structNode.identifiers.remove(0);
                }
                if (!(root.parent != null && root.parent instanceof InnerNode && ((InnerNode)root.parent).type == NodeType.CATCH_BODY)) {
                    structNode.token[node.type.ordinal()] += 1;
                }
            }
            else if (node.type == NodeType.METHOD_DEF) {
                for (ASTNode ch : root.children) {
                    if (ch.getMetaInfo().equals("parameters:")) {
                        getIdentifiers(ch, structNode.identifiers);
                        break;
                    }
                }
            }
            Vector<NodeType> keywords = new Vector<>(Arrays.asList(
                NodeType.IF_COND, NodeType.LOOP_BODY, NodeType.ASSERT_COND, NodeType.RETURN_STMT, 
                NodeType.THROW_BODY, NodeType.TRY_BODY, NodeType.SWITCH_CONDITION, NodeType.CASE_BODY,
                NodeType.CATCH_BODY, NodeType.LAMBDA_EXPR, NodeType.CLASS_ARRAY_CREATOR
            ));
            if (keywords.contains(node.type)) {
                tokens += 1;
            }
        }
        Vector<String> keywordsMeta = new Vector<>(Arrays.asList(
            "continue_statement", "break_statement"
        ));
        if (keywordsMeta.contains(root.getMetaInfo())) {
            tokens += 1;
        }
        for (int i = 0; i < tokenArr.length; ++i) {
            structNode.token[i] += tokenArr[i];
        }
        if (root instanceof ActionTokenNode) {
            actionTokens.add(((ActionTokenNode)root).getMetaInfo().split("\\.")[0]);
            tokens += 1;
        }
        if (root instanceof InnerNode) {
            InnerNode node = (InnerNode)root;
            tokenArr[node.type.ordinal()] += 1;
        }
        boolean isFirst = true;
        boolean shouldDecr = root.children.size() > 0 && root.children.get(0) instanceof InnerNode && 
                            ((InnerNode)root.children.get(0)).type == NodeType.METHOD_INVOC &&
                             root instanceof InnerNode && ((InnerNode)root).type == NodeType.METHOD_INVOC;
        for (ASTNode ch : root.children) {
            StructNode child = new StructNode(StructNodeType.UNDEFINED);
            child.pr = structNode;
            structNode.childs.add(child);
            if (isFirst && shouldDecr) {
                tokenArr[NodeType.METHOD_INVOC.ordinal()] -= 1;
            }
            tokens += parseMethod(ch, tokenArr, child, actionTokens);
            if (isFirst && shouldDecr) {
                tokenArr[NodeType.METHOD_INVOC.ordinal()] += 2;
                structNode.token[NodeType.METHOD_INVOC.ordinal()] = child.token[NodeType.METHOD_INVOC.ordinal()] + 1;
            }
            isFirst = false;
        }
        if (!isFirst && shouldDecr) {
            tokenArr[NodeType.METHOD_INVOC.ordinal()] -= 1;
        }
        if (root instanceof InnerNode) {
            InnerNode node = (InnerNode)root;
            tokenArr[node.type.ordinal()] -= 1;
        }
        return tokens;
    }

    void getIdentifiers(ASTNode root, Vector<String> ids) {
        if (root instanceof IdentifierNode) {
            ids.add(((IdentifierNode)root).name);
            return;
        }
        if (root instanceof InnerNode && ((InnerNode)root).type == NodeType.METHOD_INVOC) {
            Vector<String> newIds = new Vector<>();
            for (int i = 0; i < root.children.size(); ++i) {
                getIdentifiers(root.children.get(i), newIds);
            }
            if (newIds.size() > 0) {
                newIds.remove(0);
            }
            ids.addAll(newIds);
            return;
        }
        for (ASTNode ch : root.children) {
            getIdentifiers(ch, ids);
        }
    }
}
