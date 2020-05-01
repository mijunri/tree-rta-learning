package lstar;

public interface LearningMethod {
    void setMembership(Membership membership);
    void setEquivalenceQuery(EquivalenceQuery equivalenceQuery);
    void learn();
}
