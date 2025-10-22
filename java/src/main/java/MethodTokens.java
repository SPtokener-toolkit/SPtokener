import java.util.HashSet;
import java.util.Vector;
import java.util.HashMap;

public class MethodTokens {
    HashSet<String> actionTokens;
    StructNode root;
    Vector<int[]> varVarTokens;
    Vector<int[]> varOpTokens;
    Vector<int[]> varCalleeTokens;
    String path;
    int startLine, endLine;
    int tokensCnt;
    int n; // for n-grams
    HashMap<String, Variable> variables;

    MethodTokens(String path, int startLine, int endLine, StructNode root, int n) {

        actionTokens = new HashSet<>();
        varVarTokens = new Vector<>();
        varOpTokens = new Vector<>();
        varCalleeTokens = new Vector<>();
        tokensCnt = 0;
        this.startLine = startLine;
        this.endLine = endLine;
        this.root = root;
        this.n = n;
        variables = new HashMap<>();
    }

    void computeSemanticTokens() {
        int[] token = new int[25];
        for (int i = 0; i < 25; ++i) {
            token[i] = 0;
        }
        computeSemanticTokensTree(root);
    }

    private void computeSemanticTokensTree(StructNode rootTree) {
        for (StructNode ch : rootTree.childs) {
            computeSemanticTokensTree(ch);
        }
        if (rootTree.type == StructNodeType.IDENTIFIER) {
            if (variables.containsKey(rootTree.mainIdentifier)) {
                variables.get(rootTree.mainIdentifier).setToken(rootTree.token);
            }
            else {
                Variable v = new Variable(n);
                v.setToken(rootTree.token);
                variables.put(rootTree.mainIdentifier, v);
            }
        }
        else if (rootTree.type == StructNodeType.METHOD || rootTree.type == StructNodeType.OPERATION) {
            HashSet<String> args = new HashSet<>(rootTree.identifiers);
            HashSet<String> related = getRelatedIdentifiers(args, n);
            int[] token = new int[25];
            for (int i = 0; i < 25; ++i) {
                token[i] = rootTree.token[i];
            }
            if (rootTree.mainIdentifier != "" && rootTree.mainIdentifier != "hard") {
                int[] varToken = new int[25];
                for (int i = 0; i < 25; ++i) {
                    varToken[i] = token[i];
                }
                varToken[NodeType.METHOD_INVOC.ordinal()] += 1;
                varVarTokens.add(varToken);
            }
            for (String rel : related) {
                if (!variables.containsKey(rel)) {
                    continue;
                }
                for (int i = 0; i < 25; ++i) {
                    token[i] += variables.get(rel).token[i];
                }
            }
            if (rootTree.mainIdentifier == "hard") {
                int[] varToken = new int[25];
                for (int i = 0; i < 25; ++i) {
                    varToken[i] = token[i];
                }
                varToken[NodeType.METHOD_INVOC.ordinal()] += 1;
                varVarTokens.add(varToken);
            }
            if (rootTree.type == StructNodeType.METHOD) {
                varCalleeTokens.add(token);
            }
            else {
                varOpTokens.add(token);
            }
        }
        else {
            if (rootTree.mainIdentifier == "" && rootTree.identifiers.size() != 0) {
                // Method definition
                for (String var : rootTree.identifiers) {
                    int[] token = new int[25];
                    for (int i = 0; i < 25; ++i) {
                        token[i] = 0;
                        if (i == NodeType.METHOD_DEF.ordinal()) {
                            token[i] = 1;
                        }
                    }
                    varVarTokens.add(token);
                    Variable v = new Variable(n);
                    v.setToken(token);
                    variables.put(var, v);
                }
            }
            else if (rootTree.mainIdentifier != "") {
                int[] token = new int[25];
                for (int i = 0; i < 25; ++i) {
                    token[i] = rootTree.token[i];
                }
                HashSet<String> related = getRelatedIdentifiers(new HashSet<>(rootTree.identifiers), n);
                for (String rel : related) {
                    if (!variables.containsKey(rel)) {
                        continue;
                    }
                    for (int i = 0; i < 25; ++i) {
                        token[i] += variables.get(rel).token[i];
                    }
                }
                varVarTokens.add(token);
                if (variables.containsKey(rootTree.mainIdentifier)) {
                    variables.get(rootTree.mainIdentifier).setToken(token);
                    variables.get(rootTree.mainIdentifier).addVariables(related);;
                }
                else {
                    Variable v = new Variable(n);
                    v.setToken(token);
                    v.addVariables(related);
                    variables.put(root.mainIdentifier, v);
                }
            }
        }
    }

    private HashSet<String> getRelatedIdentifiers(HashSet<String> vars, int depth) {
        HashSet<String> hs = new HashSet<>(vars);
        if (depth == 0) {
            return hs;
        }
        for (String var : vars) {
            if (!variables.containsKey(var)) {
                continue;
            }
            HashSet<String> relatedVariables = getRelatedIdentifiers(variables.get(var).getRelatedVariables(), depth - 1);
            hs.addAll(relatedVariables);
        }
        return hs;
    }
}