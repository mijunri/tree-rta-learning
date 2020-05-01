package lstar;

import rta.RTA;
import words.TimeWords;

public class RTAMembership implements Membership {
    private RTA rta;
    private int count = 0;
    public RTAMembership(RTA rta) {
        this.rta = rta;
    }

    @Override
    public boolean answer(TimeWords words) {
        count++;
        return rta.isAccepted(words);
    }
    @Override
    public int getCount(){
        return count;
    }
}
