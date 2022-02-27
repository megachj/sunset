package sunset.java.generics;

import java.lang.reflect.Type;

import static sunset.java.generics.GenericType.*;

public class TypeErasure {

    public static class MyOptional <T> {
        T t;

        private MyOptional(T t){
            this.t = t;
        }

        public static <T> MyOptional<T> ofNullable(T t){
            return new MyOptional<>(t);
        }

        public T get(){
            return this.t;
        }

        public Type getTypeName(){
            try {
                return this.getClass().getDeclaredField("t").getType();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static class MyOptionalPerson <T extends Person> {
        T t;

        private MyOptionalPerson(T t){
            this.t = t;
        }

        public static <T extends Person> MyOptionalPerson<T> ofNullable(T t){
            return new MyOptionalPerson<>(t);
        }

        public T get(){
            return this.t;
        }

        public Type getTypeName(){
            try {
                return this.getClass().getDeclaredField("t").getType();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
