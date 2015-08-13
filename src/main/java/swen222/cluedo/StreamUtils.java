package swen222.cluedo;

import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamUtils {

    public static class Tuple<A, B> {
        public final A a;
        public final B b;

        public Tuple(A a, B b) {
            this.a = a;
            this.b = b;
        }

    }

    //Via http://stackoverflow.com/questions/17640754/zipping-streams-using-jdk8-with-lambda-java-util-stream-streams-zip
    public static<A, B, C> Stream<Tuple<A, B>> zip(Stream<? extends A> a,
                                         Stream<? extends B> b) {
        @SuppressWarnings("unchecked")
        Spliterator<A> aSpliterator = (Spliterator<A>) Objects.requireNonNull(a).spliterator();
        @SuppressWarnings("unchecked")
        Spliterator<B> bSpliterator = (Spliterator<B>) Objects.requireNonNull(b).spliterator();

        // Zipping looses DISTINCT and SORTED characteristics
        int both = aSpliterator.characteristics() & bSpliterator.characteristics() &
                ~(Spliterator.DISTINCT | Spliterator.SORTED);

        long zipSize = ((both & Spliterator.SIZED) != 0)
                ? Math.min(aSpliterator.getExactSizeIfKnown(), bSpliterator.getExactSizeIfKnown())
                : -1;

        Iterator<A> aIterator = Spliterators.iterator(aSpliterator);
        Iterator<B> bIterator = Spliterators.iterator(bSpliterator);
        Iterator<Tuple<A, B>> cIterator = new Iterator<Tuple<A, B>>() {
            @Override
            public boolean hasNext() {
                return aIterator.hasNext() && bIterator.hasNext();
            }

            @Override
            public Tuple<A,B> next() {
                return new Tuple<>(aIterator.next(), bIterator.next());
            }
        };

        Spliterator<Tuple<A, B>> split = Spliterators.spliterator(cIterator, zipSize, both);
        return (a.isParallel() || b.isParallel())
                ? StreamSupport.stream(split, true)
                : StreamSupport.stream(split, false);
    }
}
