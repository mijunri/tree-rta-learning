package lstar;

import rta.RTA;
import words.TimeWords;

import java.util.Set;

public interface LearningMethod {

    void learn();

    void refine(TimeWords timeWords);

    void buildHypothesis();

    RTA getFinalHypothesis();


}
