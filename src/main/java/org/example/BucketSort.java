package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BucketSort {

    static long iterations; // переменная для подсчета итераций

    public static void bucketSort(double[] arr) {
        iterations = 0; // сбрасываем счетчик итераций перед каждой сортировкой
        int n = arr.length;

        // 1. создание "корзин" (buckets)
        List<Double>[] buckets = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            buckets[i] = new ArrayList<>();
            iterations++; // увеличиваем счетчик для каждой созданной корзины
        }

        // 2. распределение элементов по корзинам
        for (int i = 0; i < n; i++) {
            int bucketIndex = (int) (arr[i] * n); // предполагаем, что значения находятся в диапазоне [0, 1)
            buckets[bucketIndex].add(arr[i]);
            iterations++; // увеличиваем счетчик для каждого добавленного элемента
        }

        // 3. сортировка каждой корзины
        for (int i = 0; i < n; i++) {
            Collections.sort(buckets[i]); // внутри collections.sort итерируется по элементам корзины, но подсчет внутри него затруднителен
            iterations += buckets[i].size(); // приближенная оценка.
        }

        // 4. объединение корзин в отсортированный массив
        int index = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < buckets[i].size(); j++) {
                arr[index++] = buckets[i].get(j);
                iterations++; // увеличиваем счетчик для каждого элемента
            }
        }
    }

    public static double[] generateRandomArray(int size) {
        double[] arr = new double[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            arr[i] = random.nextDouble(); // генерируем случайные числа в диапазоне [0, 1)
        }
        return arr;
    }

    public static void main(String[] args) {
        int numSizes = 50; // количество наборов данных
        int minSize = 100; // минимальный размер массива
        int maxSize = 10000; // максимальный размер массива
        int numRuns = 2; // количество запусков алгоритма для усреднения результатов
        String filename = "bucket_sort_results.csv"; // имя файла для записи результатов

        Random random = new Random();

        try (FileWriter writer = new FileWriter(filename)) {
            // записываем заголовок файла
            writer.append("Размер массива,Время (мсек),Итерации\n");

            for (int i = 0; i < numSizes; i++) {
                int size = random.nextInt(maxSize - minSize + 1) + minSize; // генерируем случайный размер

                long totalTime = 0;
                long totalIterations = 0;

                for (int run = 0; run < numRuns; run++) {
                    double[] arr = generateRandomArray(size);

                    long startTime = System.nanoTime();
                    bucketSort(arr.clone()); // клонируем, чтобы не сортировать один и тот же массив
                    long stopTime = System.nanoTime();

                    totalTime += (stopTime - startTime) / 1_000_000; // наносек -> милисек
                    totalIterations += iterations;
                }

                long avgTime = totalTime / numRuns;
                long avgIterations = totalIterations / numRuns;

                // записываем данные в файл в формате csv
                writer.append(String.format("%d,%d,%d\n", size, avgTime, avgIterations));
            }

            System.out.println("результаты записаны в файл: " + filename); // сообщение в консоль
        } catch (IOException e) {
            System.err.println("ошибка при записи в файл: " + e.getMessage()); // сообщение об ошибке
        }
    }
}