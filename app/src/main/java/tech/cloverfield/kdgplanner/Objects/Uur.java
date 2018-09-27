package tech.cloverfield.kdgplanner.Objects;

import java.util.Date;

public class Uur {

    private Date startUur;
    private Date endUur;

    public Uur(Date start, Date end) {
        this.startUur = start;
        this.endUur = end;
    }

    public Date getStart() {
        return startUur;
    }

    public Date getEnd() {
        return endUur;
    }
}
