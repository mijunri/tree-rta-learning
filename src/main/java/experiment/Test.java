package experiment;

import lstar.*;
import observationTable.ObservationTable;
import rta.RTA;
import rta.RTABuilder;
import ttt.DTree;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        String base = ".\\src\\main\\resources\\rtaJson\\4_4_4\\";
        String path = base+"4_4_4.json";
        RTA rta = RTABuilder.getRTAFromJsonFile(path);
        Membership membership1 = new RTAMembership(rta);
        EquivalenceQuery equivalenceQuery1 = new RTAEquivalenceQuery(rta);
        LearningMethod observationTable = new ObservationTable("hypothesis1",rta.getSigma(),membership1,equivalenceQuery1);
        Learner observationTableLearner = new Learner(membership1,equivalenceQuery1,observationTable);
        observationTableLearner.doExperiment();
    }
}
