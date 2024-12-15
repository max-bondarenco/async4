import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        List<String> files = List.of("file1.txt", "file2.txt", "file3.txt");
        long startTime = System.currentTimeMillis();

        // Reading sentences
        List<CompletableFuture<String>> fileReadFutures = files.stream()
                .map(Main::readFileAsync)
                .toList();

        List<String> sentences = CompletableFuture.allOf(fileReadFutures.toArray(new CompletableFuture[0]))
                .thenApply(_ ->
                fileReadFutures.stream()
                        .map(CompletableFuture::join)
                        .toList()
        ).get();

        // Processing sentences
        List<CompletableFuture<String>> processedFutures = sentences.stream()
                .map(Main::removeLettersAsync)
                .toList();

        List<String> processedResults = CompletableFuture.allOf(processedFutures.toArray(new CompletableFuture[0]))
                .thenApply(_ ->
                processedFutures.stream()
                        .map(CompletableFuture::join)
                        .toList()
        ).get();

        String result = processedResults.stream().reduce("", String::concat);
        long endTime = System.currentTimeMillis();
        System.out.printf("\nFinal result: %s\nFinished in %dms",result,endTime-startTime);
    }

    private static CompletableFuture<String> readFileAsync(String fileName) {
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();

            try {
                Path path = Paths.get(System.getProperty("user.dir") + "\\src\\" + fileName);
                String result = Files.readString(path).trim();
                long endTime = System.currentTimeMillis();
                System.out.printf("File %s read in %dms\nContent: %s\n", fileName, endTime-startTime, result);
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
            System.out.println("Processed sentence in " + (endTime - startTime) + "ms");
            return result;
        });
    }
}