package lstar;

import rta.RTA;
import words.TimeWords;

public interface EquivalenceQuery {
    int getCount();
    TimeWords findCounterExample(RTA rta);
}
