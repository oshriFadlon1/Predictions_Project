package dtos.user;

public class DtoTerminationForRequest {
    private int seconds;
    private int ticks;
    private boolean freeChoice;

    public DtoTerminationForRequest(int seconds, int ticks, boolean freeChoice) {
        this.seconds = seconds;
        this.ticks = ticks;
        this.freeChoice = freeChoice;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getTicks() {
        return ticks;
    }

    public boolean isFreeChoice() {
        return freeChoice;
    }
}
