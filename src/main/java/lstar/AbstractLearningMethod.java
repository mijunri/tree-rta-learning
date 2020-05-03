package lstar;

import rta.RTA;
import words.TimeWords;

import java.util.Set;

public abstract class AbstractLearningMethod implements LearningMethod {
    private String name;
    private Set<String> sigma;
    private Membership membership;
    private EquivalenceQuery equivalenceQuery;
    private RTA hypothesis;
    private RTA finalHypothesis;

    public AbstractLearningMethod(String name, Set<String> sigma, Membership membership, EquivalenceQuery equivalenceQuery) {
        this.name = name;
        this.sigma = sigma;
        this.membership = membership;
        this.equivalenceQuery = equivalenceQuery;
    }

    @Override
    public void learn() {
        TimeWords ce = null;
        buildHypothesis();
//        System.out.println(hypothesis);
        while ((ce = equivalenceQuery.findCounterExample(hypothesis)) != null){
            do{
//                System.out.println(ce);
                refine(ce);
                buildHypothesis();
//                System.out.println(hypothesis);
            }while (answer(ce) != hypothesis.isAccepted(ce));
        }
        finalHypothesis = hypothesis;
    }

    @Override
    public RTA getFinalHypothesis() {
        return finalHypothesis;
    }

    public boolean answer(TimeWords timeWords){
        return membership.answer(timeWords);
    }

    public Set<String> getSigma(){
        return sigma;
    }

    public void setHypothesis(RTA hypothesis){
        this.hypothesis = hypothesis;
    }

    public RTA getHypothesis(){
        return hypothesis;
    }

    public String getName(){
        return name;
    }
}
