package lstar;

import rta.RTA;
import words.TimeWords;

import java.util.Set;

public interface EquivalenceQuery {
    int getCount();
    TimeWords findCounterExample(RTA rta);
    Set<String> getSigma();
}
