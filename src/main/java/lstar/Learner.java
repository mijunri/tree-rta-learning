package lstar;

import lstar.EquivalenceQuery;
import lstar.LearningMethod;
import lstar.Membership;
import rta.RTA;

public class Learner {

    private Membership membership;
    private EquivalenceQuery equivalenceQuery;
    private LearningMethod learnMethod;
    private int membershipCount;
    private int equivalenceQueryCount;
    private long costTime;

    public Learner(Membership membership, EquivalenceQuery equivalenceQuery, LearningMethod learnMethod) {
        this.membership = membership;
        this.equivalenceQuery = equivalenceQuery;
        this.learnMethod = learnMethod;
    }

    public void doExperiment(){
        long start = System.currentTimeMillis();
        learnMethod.learn();
        long end = System.currentTimeMillis();
        costTime = end-start;
        membershipCount = membership.getCount();
        equivalenceQueryCount = equivalenceQuery.getCount();
    }

    public int getMembershipCount() {
        return membershipCount;
    }

    public int getEquivalenceQueryCount() {
        return equivalenceQueryCount;
    }

    public long getCostTime() {
        return costTime;
    }
}
