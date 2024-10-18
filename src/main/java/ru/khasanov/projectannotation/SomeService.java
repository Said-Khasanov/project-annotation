package ru.khasanov.projectannotation;

import org.springframework.stereotype.Service;

@Service
public class SomeService {

    @PerformanceAnalyze
    public void print(int a, String text) {
        for (int i = 0; i < a; i++) {
            System.out.println(text);
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
