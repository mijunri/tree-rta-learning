package lstar;

import rta.Location;
import rta.RTA;
import rta.RTABuilder;
import rta.Transition;
import words.TimeWord;
import words.TimeWords;
import words.TimeWordsUtil;

import java.util.*;

public class RTAEquivalenceQuery implements EquivalenceQuery{
    private RTA rta;
    private int count;

    public RTAEquivalenceQuery(RTA rta){
        this.rta = rta;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public TimeWords findCounterExample(RTA hypotheses){
        count++;

        RTA negRTA = RTABuilder.getNegtiveRTA(rta);
        RTA negHypothesis = RTABuilder.getNegtiveRTA(hypotheses);

        RTA r1 = RTABuilder.getCartesian(negRTA,hypotheses);
        TimeWords w1 = counterExample(r1);


        RTA r2 = RTABuilder.getCartesian(rta,negHypothesis);
        TimeWords w2 = counterExample(r2);

        if(w1==null){
            return w2;
        }

        if(w2==null){
            return w1;
        }

        return w1.size()<=w2.size()?w1:w2;
    }

    @Override
    public Set<String> getSigma() {
        return rta.getSigma();
    }


    private TimeWords counterExample(RTA rta){
        Set<Location> visited = new HashSet<>();
        Map<Location,TimeWords> map = new HashMap<>();
        Deque<Location> queue = new LinkedList<>();

        Location initLocation = rta.getInitLocation();
        visited.add(initLocation);
        map.put(initLocation,TimeWords.EMPTY_WORDS);
        queue.offer(initLocation);
        while(!queue.isEmpty()){
            Location current = queue.poll();
            List<Transition> transitions = rta.getTransitions(current,null,null);
            for(Transition t:transitions){
                Location source = t.getSourceLocation();
                Location target = t.getTargetLocation();
                TimeWords locationWords = map.get(source);
                if(!visited.contains(target)){
                    visited.add(target);
                    TimeWord word = t.toWord();
                    TimeWords words = TimeWordsUtil.concat(locationWords,word);
                    map.put(target,words);
                    queue.offer(target);
                }
            }
        }

        List<Location> acceptedLocations = rta.getAcceptedLocations();
        TimeWords ce = null;
        Set<TimeWords> set = new HashSet<>();
        for(Location l:acceptedLocations){
            if(map.containsKey(l)){
                TimeWords t = map.get(l);
                set.add(t);
                if(ce == null){
                    ce = t;
                }else {
                    ce = ce.size()<=t.size()?ce:t;
                }
            }
        }
        return ce;
    }
}
