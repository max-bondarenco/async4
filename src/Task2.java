import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class Task2 {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        CompletableFuture<List<Double>> generateNumbers = CompletableFuture.supplyAsync(() -> {
            Random random = new Random();
            return IntStream.range(0, 20)
                    .mapToDouble(i -> random.nextDouble() * 100)
                    .boxed()
                    .toList();
        });

        CompletableFuture<Double> calculateSum = generateNumbers.thenApply(numbers -> {
            double result = 0;
            for (int i = 0; i < numbers.size() - 1; i++)
                result += numbers.get(i) * numbers.get(i + 1);
            return result;
        });

        generateNumbers.thenAccept(numbers -> {
            long endTime = System.currentTimeMillis();
            System.out.printf("[Generated numbers in %dms]: %s\n", endTime-startTime, numbers.toString());
        });

        calculateSum.thenAccept(result -> {
            long endTime = System.currentTimeMillis();
            System.out.printf("[Calculated in %dms]: %s\n", endTime-startTime, result);
        });

        CompletableFuture.allOf(generateNumbers, calculateSum).join();

        long endTime = System.currentTimeMillis();
        System.out.printf("Total time: %dms\n",endTime-startTime);
    }
}