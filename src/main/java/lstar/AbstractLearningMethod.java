package lstar;

import rta.RTA;
import words.TimeWord;
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
        show();
        System.out.println(hypothesis);
        while ((ce = equivalenceQuery.findCounterExample(hypothesis)) != null){
            if(ce.getWordList().get(0).equals(new TimeWord("a",5.0))
            && ce.getWordList().get(1).equals(new TimeWord("a",4.0)) ){
                ce = new TimeWords(new TimeWord("a",0),new TimeWord("a",4));
            }
            do{
                System.out.println(ce);
                refine(ce);
                buildHypothesis();
                show();
                System.out.println(hypothesis);
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


    public void show(){

    }
}
