package lstar;

import observationTable.ObservationTable;
import org.junit.Test;
import rta.RTA;
import rta.RTABuilder;
import ttt.DTree;

import java.io.IOException;

import static org.junit.Assert.*;

public class LearnerTest {

    @Test
    public void doExperiment() throws IOException {
        RTA a = RTABuilder.getRTAFromJsonFile(".\\src\\main\\resources\\rtaJson\\14_4_4\\14_4_4-20.json");
        Membership membership = new RTAMembership(a);
        EquivalenceQuery equivalenceQuery = new RTAEquivalenceQuery(a);
        LearningMethod learningMethod = new DTree("hypothesis",a.getSigma(),membership,equivalenceQuery);

        Learner learner = new Learner(membership,equivalenceQuery,learningMethod);
        learner.doExperiment();
        System.out.println("耗时："+learner.getCostTime()+"ms");
        System.out.println("等价查询："+learner.getEquivalenceQueryCount());
        System.out.println("成员查询："+learner.getMembershipCount());

    }
}