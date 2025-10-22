import java.util.StringJoiner;
import java.util.stream.IntStream;

import static java.lang.Math.sqrt;

public class Token {
    private int[] token;

    Token(int[] token) {
        this.token = token;
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(" ");
        IntStream.of(token).forEach(x -> sj.add(String.valueOf(x)));
        return sj.toString();
    }

    void addTo(float[] tok) {
        for (int i = 0; i < token.length; ++i) {
            tok[i] += token[i];
        }
    }
}
