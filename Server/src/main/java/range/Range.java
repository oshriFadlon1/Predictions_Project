package range;

import java.io.Serializable;

public class Range implements Serializable {
    private float from;
    private float to;

    public Range(float from, float to) {
        this.from = from;
        this.to = to;
    }

    public float getFrom() {
        return from;
    }

    public float getTo() {
        return to;
    }

    public void setFrom(float from) {
        this.from = from;
    }



    public void setTo(float to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "from: " + from +
                ", to:" + to;
    }

    public Range getCopyOfRange(){
        return new Range(this.from,this.to);
    }
}
