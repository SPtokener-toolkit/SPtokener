import java.util.*;

public class Index {
    Vector<Integer> tokenNumbers;
    HashSet<Integer> uniqueTokenNumbers;
    String pathDir;
    boolean isSorted;
    int tokenCountDiffer;
    boolean commonMode;

    Index(String pathDir, int tokenCountDiffer, boolean commonMode) {
        tokenNumbers = new Vector<>();
        this.commonMode = commonMode;
        uniqueTokenNumbers = new HashSet<>();
        FileWorker fw = new FileWorker(commonMode);
        fw.updateDir(pathDir);
        this.pathDir = pathDir;
        this.tokenCountDiffer = tokenCountDiffer;
    }

    void addBlock(CodeBlock block) {
        FileWorker fw = new FileWorker(commonMode);
        int id = block.getTokensNum();
        fw.addBlockDirect(pathDir, id, block);
        isSorted = false;
        if (!uniqueTokenNumbers.contains(id)) {
            uniqueTokenNumbers.add(id);
            tokenNumbers.add(id);
        }
    }

    Vector<CodeBlock> getBlocks(CodeBlock startBlock, int startIndex) {
        if (!isSorted) {
            isSorted = true;
            tokenNumbers.sort(null);
        }
        FileWorker fw = new FileWorker(commonMode);
        Vector<CodeBlock> v = new Vector<>();
        int id = startBlock.getTokensNum();
        int innerCountDiffer = Math.max(tokenCountDiffer, id / 2);
        int upper_bound = id + innerCountDiffer;
        for (int i = startIndex + 1; i < tokenNumbers.size(); ++i) {
            if (tokenNumbers.get(i) > upper_bound) {
                break;
            }
            Vector<CodeBlock> blocks = fw.readBlocks(pathDir, tokenNumbers.get(i));
            for (CodeBlock block : blocks) {
                if (block.hashCode() != startBlock.hashCode()) {
                    v.add(block);
                }
            }
        }
        return v;
    }
}
