public class CodeBlockInfo {
    String filename;
    int startLine;
    int endLine;

    CodeBlockInfo(String filename, int startLine, int endLine) {
        this.filename = filename;
        this.startLine = startLine;
        this.endLine = endLine;
    }
}
