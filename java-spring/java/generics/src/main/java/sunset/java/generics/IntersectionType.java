package sunset.java.generics;

import java.util.function.Consumer;
import java.util.function.Function;

public class IntersectionType {

    // ----------------------------------------
    // 1. marker interface

    /**
     * marker interface: 구현 메소드가 0개인 인터페이스
     *
     * Serializable, Cloneable 등
     */
    public interface LinearFuncMarker {}

    public static void printFuncResult(Function<Integer, Integer> fx, Integer a) {
        System.out.println("func result: " + fx.apply(a));
    }

    public static <T extends Function<Integer, Integer> & LinearFuncMarker> void printLinearFuncResult(T fx, Integer a) {
        System.out.println("linear func result: " + fx.apply(a));
    }

    // ----------------------------------------
    // 2. interface default method + callback

    public static <T extends Function<S, S>, S> void run(T t, Consumer<T> callback) {
        callback.accept(t);
    }
    public static <T extends Function<S, S>, S> S run(T t, Function<T, S> callback) {
        return callback.apply(t);
    }

    public interface LibraryModel extends Function<Integer, Integer> {
        default int opposite(int a) {
            return -1 * apply(a);
        }
    }

    public interface CustomModel extends Function<Integer, Integer> {
        default int remains(int a, int r) {
            return apply(a) % r;
        }
    }
}
