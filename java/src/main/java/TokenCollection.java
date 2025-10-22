import java.util.Vector;

public class TokenCollection {
    private Vector<Token> collection;

    TokenCollection() {
        collection = new Vector<>();
    }

    void add(Token t) {
        collection.add(t);
    }

    Token get(int i) {
        return collection.get(i);
    }

    int size() {
        return collection.size();
    }

    float[] getAvg() {
        float[] token = new float[25];
        for (int i = 0; i < token.length; ++i) {
            token[i] = 0;   
        }
        for (Token collectionToken: collection) {
            collectionToken.addTo(token);
        }
        if (size() != 0) {
            for (int i = 0; i < token.length; ++i) {
                token[i] /= (float) size();
            }   
        }
        return token;
    }
}
