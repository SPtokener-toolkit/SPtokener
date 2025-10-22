import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class CodeBlock {

    private TokenCollection vars;
    private TokenCollection ops;
    private TokenCollection callees;

    private Vector<String> activeTokens;
    private int id;

    private int tokensNum;

    private CodeBlockInfo info;

    CodeBlock(int id) {
        activeTokens = new Vector<>();
        vars = new TokenCollection();
        ops = new TokenCollection();
        callees = new TokenCollection();
        this.id = id;
    }

    void addActiveToken(String token) {
        activeTokens.add(token);
    }

    void setCollection(TokenCollection collection, CollectionType type) {
        if (type == CollectionType.VAR) {
            vars = collection;
        }
        else if (type == CollectionType.OPERATION) {
            ops = collection;
        }
        else if (type == CollectionType.CALLEE) {
            callees = collection;
        }
    }

    Token getToken(int i, CollectionType type) {
        if (type == CollectionType.VAR) {
            return vars.get(i);
        }
        if (type == CollectionType.OPERATION) {
            return ops.get(i);
        }
        if (type == CollectionType.CALLEE) {
            return callees.get(i);
        }
        return null;
    }

    int collectionSize(CollectionType type) {
        if (type == CollectionType.VAR) {
            return vars.size();
        }
        if (type == CollectionType.OPERATION) {
            return ops.size();
        }
        if (type == CollectionType.CALLEE) {
            return callees.size();
        }
        return 0;
    }

    Vector<String> getActiveTokens() {
        return activeTokens;
    }

    @Override
    public int hashCode() {
        return id;
    }

    int activeTokensOverlap(CodeBlock other) {
        Set<String> tokens = new HashSet<>(activeTokens);
        int cnt = 0;
        for (String token : other.activeTokens) {
            if (tokens.contains(token)) {
                cnt++;
            }
        }
        return cnt;
    }

    boolean shouldBeFiltered(CodeBlock other, float beta, float theta) {
        int targetTokens = tokensNum;
        int candidateTokens = other.tokensNum;
        float tokenRatio = min(targetTokens, candidateTokens) / (float) max(targetTokens, candidateTokens);
        if (tokenRatio <= theta) {
            return true;
        }
        int overlap = activeTokensOverlap(other);
        float overlapRatio = overlap / (float) min(activeTokens.size(), other.activeTokens.size());

        return overlapRatio <= beta;
    }


    void setInfo(CodeBlockInfo info) {
        this.info = info;
    }

    CodeBlockInfo getInfo() {
        return info;
    }

    void setTokensNum(int tokens) {
        tokensNum = tokens;
    }

    int getTokensNum() {
        return tokensNum;
    }

    float[] getCollectionAvg(CollectionType type) {
        if (type == CollectionType.VAR) {
            return vars.getAvg();
        }
        if (type == CollectionType.OPERATION) {
            return ops.getAvg();
        }
        if (type == CollectionType.CALLEE) {
            return callees.getAvg();
        }
        return new float[25];
    }
}
