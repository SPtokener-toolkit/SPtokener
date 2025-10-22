import java.util.Vector;

public class Processor {

    float beta; // Same active token ratio threshold
    float theta; // Total token ratio threshold
    float eta; // Block similarity threshold
    Processor(float beta, float theta, float eta) {
        this.beta = beta;
        this.theta = theta;
        this.eta = eta;
    }

    float getSimilarity(CodeBlock first, CodeBlock second, CollectionType type) {
        if (first.collectionSize(type) == 0 && second.collectionSize(type) == 0) {
            return 1;
        }
        float[] firstAvg = first.getCollectionAvg(type);
        float[] secondAvg = second.getCollectionAvg(type);
        float result = 0;
        float firstLen = 0;
        float secondLen = 0;
        for (int i = 0; i < firstAvg.length; ++i) {
            firstLen += firstAvg[i] * firstAvg[i];
            secondLen += secondAvg[i] * secondAvg[i];
            result += firstAvg[i] * secondAvg[i];
        }
        return (float)(result / Math.sqrt(firstLen) / Math.sqrt(secondLen));
    }

    Vector<ClonePair> getClonePairs(CodeBlock block, Index ind, int startIndex) {
        if (block.getTokensNum() <= 15) {
            return new Vector<>();
        }
        Vector<ClonePair> pairs = new Vector<>();
        Vector<CodeBlock> candidates = ind.getBlocks(block, startIndex);
        for (CodeBlock otherBlock : candidates) {
            if (otherBlock.getTokensNum() == block.getTokensNum() && 
                otherBlock.hashCode() >= block.hashCode()) {
                continue;
            }
            boolean shouldFilter = block.shouldBeFiltered(otherBlock, beta, theta);
            if (shouldFilter) {
                continue;
            }

            float varSim = getSimilarity(block, otherBlock, CollectionType.VAR);
            float operationSim = getSimilarity(block, otherBlock, CollectionType.OPERATION);
            float calleeSim = getSimilarity(block, otherBlock, CollectionType.CALLEE);
            float sim = (varSim + operationSim + calleeSim) / 3;
            if (sim > eta) {
                pairs.add(new ClonePair(block.getInfo(), otherBlock.getInfo()));
            }
        }
        return pairs;
    }
}
