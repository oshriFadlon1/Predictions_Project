package dtos.requestInfo;

public class DtoTerminationSimulationInfo {

    private final int ticks;
    private final int seconds;
    private final boolean byUser;

    public DtoTerminationSimulationInfo(int ticks,
                                        int seconds,
                                        boolean byUser) {
        this.ticks = ticks;
        this.seconds = seconds;
        this.byUser = byUser;
    }

    public int getTicks() {
        return ticks;
    }

    public int getSeconds() {
        return seconds;
    }

    public boolean isByUser() {
        return byUser;
    }

    @Override
    public String toString() {
        if (byUser){
            return "End by user choice";
        }
        else {
            String res = "";
            if (ticks != -1){
                res += "Ticks: "+ this.ticks;
            }
            if (this.seconds != -1){
                res += "Seconds: "+ this.seconds;
            }
            return res;
        }
    }
}
