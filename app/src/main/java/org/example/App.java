package org.example;

import org.example.customLinkedList.CustomLinkedList;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class App {

    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        CustomLinkedList<String> list = new CustomLinkedList<>();

        list.add("Олег");
        list.add("Генадий");
        list.add("Виктор");

        System.out.println("Элемент с нулевым индексом: " + list.get(0) + "\n");

        System.out.println("Удалён элемент с нулевым индексом: " + list.remove(0) + "\n");

        System.out.println("Список содержит \"Олег\": " + list.contains("Олег"));
        System.out.println("Список содержит \"Генадий\": " + list.contains("Генадий") + "\n");

        List<String> collection = Arrays.asList("Мария", "Ангелина", "Елизавета");
        list.addAll(collection);

        System.out.println("Все элементы списка:");
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }

        Stream<Integer> stream = Stream.of(11, 25, 37, 43, 50);

        CustomLinkedList<Integer> customLinkedList = stream.reduce(
                new CustomLinkedList<>(),
                (customList, element) -> {
                    customList.add(element);
                    return customList;
                },
                (list1, list2) -> {
                    list1.addAll(list2);
                    return list1;
                });

        System.out.println("Элементы в CustomLinkedList после преобразования из произвольного стрима: ");
        for (int i = 0; i < customLinkedList.size(); i++) {
            System.out.println(customLinkedList.get(i));
        }
    }

}
