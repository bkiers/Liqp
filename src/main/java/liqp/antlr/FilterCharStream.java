package liqp.antlr;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.misc.Interval;

public class FilterCharStream implements CharStream {
    protected CharStream in;

    public FilterCharStream(CharStream in) {
        this.in = in;
    }

    @Override
    public String getText(Interval interval) {
        return in.getText(interval);
    }

    @Override
    public void consume() {
        in.consume();
    }

    @Override
    public int LA(int i) {
        return in.LA(i);
    }

    @Override
    public int mark() {
        return in.mark();
    }

    @Override
    public void release(int marker) {
        in.release(marker);
    }

    @Override
    public int index() {
        return in.index();
    }

    @Override
    public void seek(int index) {
        in.seek(index);
    }

    @Override
    public int size() {
        return in.size();
    }

    @Override
    public String getSourceName() {
        return in.getSourceName();
    }
}
