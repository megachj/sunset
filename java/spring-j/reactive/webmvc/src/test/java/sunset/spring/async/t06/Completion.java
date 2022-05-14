package sunset.spring.async.t06;

import lombok.NoArgsConstructor;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.function.Consumer;
import java.util.function.Function;

@NoArgsConstructor
public class Completion<S, T> {
    protected Completion next;

    public void andAccept(Consumer<T> con) {
        Completion<T, Void> c = new AcceptCompletion<>(con);
        this.next = c;
    }

    public Completion<T, T> andError(Consumer<Throwable> eCon) {
        Completion<T, T> c = new ErrorCompletion<>(eCon);
        this.next = c;
        return c;
    }

    public <V> Completion<T, V> andApply(Function<T, ListenableFuture<V>> fn) {
        Completion<T, V> c = new ApplyCompletion<>(fn);
        this.next = c;
        return c;
    }

    public static <S, T> Completion<S, T> from(ListenableFuture<T> lf) {
        Completion<S, T> c = new Completion<>();
        lf.addCallback(s -> {
            c.complete(s);
        }, e -> {
            c.error(e);
        });
        return c;
    }

    protected void complete(T s) {
        if (next != null) next.run(s);
    }

    protected void error(Throwable e) {
        if (next != null) next.error(e);
    }

    protected void run(S value) {
    }
}

class AcceptCompletion<S> extends Completion<S, Void> {
    private Consumer<S> con;

    public AcceptCompletion(Consumer<S> con) {
        this.con = con;
    }

    @Override
    protected void run(S value) {
        con.accept(value);
    }
}

class ErrorCompletion<T> extends Completion<T, T> {

    private Consumer<Throwable> eCon;

    public ErrorCompletion(Consumer<Throwable> eCon) {
        this.eCon = eCon;
    }

    @Override
    protected void run(T value) {
        if (next != null) next.run(value);
    }

    @Override
    protected void error(Throwable e) {
        eCon.accept(e);
    }
}

class ApplyCompletion<S, T> extends Completion<S, T> {

    private Function<S, ListenableFuture<T>> fn;

    public ApplyCompletion(Function<S, ListenableFuture<T>> fn) {
        this.fn = fn;
    }

    @Override
    protected void run(S value) {
        ListenableFuture<T> lf = fn.apply(value);
        lf.addCallback(s -> {
            complete(s);
        }, e -> {
            error(e);
        });
    }
}
