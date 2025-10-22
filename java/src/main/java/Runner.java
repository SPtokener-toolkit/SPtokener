public class Runner {
    public static void main(String[] args) {
        String indexPath = "./index";
        String tokenPath = "./tokens";
        String[] parsedArgs = args[0].split("\s");
        int nproc = 4;

        boolean commonMode = !(parsedArgs.length >= 2 && parsedArgs[1].equals("--bcb"));
        long startTime = System.currentTimeMillis();
        FileWorker worker = new FileWorker(commonMode);
        worker.processAll(parsedArgs[0], indexPath, tokenPath, nproc);
        long endTime = System.currentTimeMillis();
        System.out.println(String.format("%dms", endTime - startTime));
    }
}
