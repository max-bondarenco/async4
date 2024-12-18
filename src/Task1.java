import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Task1 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        List<String> files = List.of("file1.txt", "file2.txt", "file3.txt");
        long startTime = System.currentTimeMillis();

        // Reading sentences
        List<CompletableFuture<String>> fileReadFutures = files.stream()
                .map(Task1::readFileAsync)
                .toList();

        List<String> sentences = CompletableFuture.allOf(fileReadFutures.toArray(new CompletableFuture[0]))
                .thenApply(_ ->
                fileReadFutures.stream()
                        .map(CompletableFuture::join)
                        .toList()
        ).get();

        // Processing sentences
        List<CompletableFuture<String>> processedFutures = sentences.stream()
                .map(Task1::removeLettersAsync)
                .toList();

        List<String> processedResults = CompletableFuture.allOf(processedFutures.toArray(new CompletableFuture[0]))
                .thenApply(_ ->
                processedFutures.stream()
                        .map(CompletableFuture::join)
                        .toList()
        ).get();

        String result = processedResults.stream().reduce("", String::concat);
        long endTime = System.currentTimeMillis();
        System.out.printf("[Finished in %dms]: %s",endTime-startTime,result);
    }

    private static CompletableFuture<String> readFileAsync(String fileName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                long startTime = System.currentTimeMillis();
                Path path = Paths.get(System.getProperty("user.dir") + "\\" + fileName);
                String result = Files.readString(path).trim();
                long endTime = System.currentTimeMillis();
                System.out.printf("[%s read in %dms]: %s\n", fileName, endTime-startTime, result);
                return result;
            } catch (IOException e) {
                throw new RuntimeException("Error reading file: " + fileName, e);
            }
        });
    }

    private static CompletableFuture<String> removeLettersAsync(String sentence) {
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            String result = sentence.replaceAll("[a-zA-Z]", "");
            long endTime = System.currentTimeMillis();
            System.out.printf("Processed sentence in %dms\n", endTime-startTime);
            return result;
        });
    }
}