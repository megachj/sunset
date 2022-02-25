package sunset.java.generics;

import java.util.Arrays;
import java.util.List;

public class GenericType {
    // ----------------------------------------
    public static class Wrapper<T> {
    }
    public static class Person {
        public void hello() {
            System.out.println("Hello");
        }
    }
    public static class Developer extends Person {
        @Override
        public void hello() {
            System.out.println("Hello World");
        }
    }
    public static class JavaDeveloper extends Developer {
        @Override
        public void hello() {
            System.out.println("Hello Java World");
        }
    }

    // ----------------------------------------
    public static <T extends Comparable<T>> long countGreaterThan(T[] arr, T elem) {
        return Arrays.stream(arr).filter(e -> e.compareTo(elem) > 0).count();
    }

    public static <T> void emptyMethod(T t, List<T> list) {}

    // ----------------------------------------
    public static <T> void method(List<T> list) {}
    // public static void method(List<?> list) {} // compile error: same erasure
    // public static void method(List<Object> list) {} // compile error: same erasure

    // ----------------------------------------
    public static void printList(List<Object> list) {
        list.forEach(System.out::println);
    }
    public static void printList2(List<Integer> list) {
        list.forEach(System.out::println);
    }
}
