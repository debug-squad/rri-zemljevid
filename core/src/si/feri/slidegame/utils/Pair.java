package si.feri.slidegame.utils;

public class Pair<A, B> {
    public A fst;
    public B snd;

    public Pair(A fst, B snd) {
        this.fst = fst;
        this.snd = snd;
    }

    public static <A, B> Pair<A, B> of(A fst, B snd) {
        return new Pair(fst, snd);
    }
}
