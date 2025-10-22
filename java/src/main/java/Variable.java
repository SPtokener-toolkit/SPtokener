import java.util.Vector;
import java.util.HashSet;   

public class Variable {
    int[] token;
    Vector<HashSet<String>> window;
    int windowPtr;

    Variable(int k) {
        windowPtr = 0;
        window = new Vector<>();
        for (int i = 0; i < k; ++i) {
            window.add(new HashSet<>());
        }
        token = new int[25];
        for (int i = 0; i < 25; ++i) {
            token[i] = 0;
        }
    }

    void addVariables(HashSet<String> vars) {
        window.set(windowPtr, vars);
        windowPtr = (windowPtr + 1) % window.size();
    }

    void setToken(int[] newToken) {
        for (int i = 0; i < 25; ++i) {
            token[i] = newToken[i];
        }
    }

    HashSet<String> getRelatedVariables() {
        HashSet<String> hs = new HashSet<>();
        for (HashSet<String> vars : window) {
            for (String v : vars) {
                hs.add(v);
            }
        }
        return hs;
    }
}
