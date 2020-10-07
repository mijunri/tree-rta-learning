package observationTable;

import lstar.AbstractLearningMethod;
import lstar.EquivalenceQuery;
import lstar.Membership;
import rta.*;
import words.TimeWord;
import words.TimeWords;
import words.TimeWordsUtil;

import java.util.*;

public class ObservationTable extends AbstractLearningMethod {

    private Set<TimeWords> s = new HashSet<>();
    private Set<TimeWords> r = new HashSet<>();
    private List<TimeWords> suffixes = new ArrayList<>();
    private Map<Pair, Boolean> answers = new HashMap<>();



    public ObservationTable(String name,Set<String> sigma, Membership membership,EquivalenceQuery equivalenceQuery) {
        super(name,sigma,membership,equivalenceQuery);

        suffixes.add(TimeWords.EMPTY_WORDS);
        s.add(TimeWords.EMPTY_WORDS);
        for(String action:sigma){
            TimeWords timeWords = TimeWordsUtil.actionWords(action);
            r.add(timeWords);
        }

        fillTable(s);
        fillTable(r);

        makePrapared();
    }

    private void makePrapared(){
        while (true){
            if(!isClosed()){
                makeClosed();
                continue;
            }
            if(!isConsistent()){
                makeConsistent();
                continue;
            }
            if(!isEvidClosed()){
                makeEvidClosed();
                continue;
            }
            break;
        }
    }



    private void fillTable(Set<TimeWords> set){
        for(TimeWords prefixWords:set){
            for(TimeWords suffixWords:suffixes){
                Pair pair = new Pair(prefixWords,suffixWords);
                if(!answers.containsKey(pair)){
                    TimeWords timeWords = pair.timeWords();
                    boolean isAccpted = answer(timeWords);
                    answers.put(pair,isAccpted);
                }
            }
        }
    }



    public Row getRow(TimeWords timeWords){
        if(!s.contains(timeWords) && !r.contains(timeWords)){
            return null;
        }
        Row row = new Row();
        for(TimeWords suffixWords: suffixes){
            Pair pair = new Pair(timeWords,suffixWords);
            boolean answer = answers.get(pair);
            row.add(answer);
        }
        return row;
    }

    public Set<Row> getRowSet(Set<TimeWords> timeWordsSet){
        Set<Row> rowSet = new HashSet<>();
        if(timeWordsSet != null){
            for(TimeWords words: timeWordsSet){
                rowSet.add(getRow(words));
            }
        }
        return rowSet;
    }




    public boolean isClosed() {
        Set<Row> sRowSet = getRowSet(s);
        Set<Row> rRowSet = getRowSet(r);
        return sRowSet.containsAll(rRowSet);
    }

    public void makeClosed(){
        Set<Row> sRowSet = getRowSet(s);
        for(TimeWords words:r){
            if(!sRowSet.contains(getRow(words))){
                s.add(words);
                r.remove(words);
                for(String action: getSigma()){
                    TimeWords actionWords = TimeWordsUtil.actionWords(action);
                    TimeWords newWords = TimeWordsUtil.concat(words,actionWords);
                    if(!s.contains(newWords) && !r.contains(newWords)){
                        r.add(newWords);
                    }
                }
                break;
            }
        }
        fillTable(r);
    }


    private List<TimeWords> unConsistentCouple = null;
    private TimeWord key = null;
    public boolean isConsistent() {
        unConsistentCouple = new ArrayList<>();
        Set<TimeWord> lastWordSet = getLastWordSet();
        List<TimeWords> list = getPrefixList();
        for(int i = 0; i < list.size(); i++){
            Row sRow = getRow(list.get(i));
            for(int j = i + 1; j < list.size(); j ++){
                if(sRow.equals(getRow(list.get(j)))){
                    for(TimeWord lastWord: lastWordSet){
                        TimeWords words1 = TimeWordsUtil.concat(list.get(i),lastWord);
                        TimeWords words2 = TimeWordsUtil.concat(list.get(j),lastWord);
                        Row row1 = getRow(words1);
                        Row row2 = getRow(words2);
                        if(row1!=null && row2!=null && !row1.equals(row2)){
                            unConsistentCouple.add(words1);
                            unConsistentCouple.add(words2);
                            key = lastWord;
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }



    public void makeConsistent(){
        TimeWords words1 = unConsistentCouple.get(0);
        TimeWords words2 = unConsistentCouple.get(1);
        for(TimeWords w:suffixes){
            Pair pair1 = new Pair(words1,w);
            Pair pair2 = new Pair(words2,w);

            boolean answer1 = answers.get(pair1);
            boolean answer2 = answers.get(pair2);
            if(!answer1 == answer2){
                TimeWords words = TimeWordsUtil.concat(key,w);
                suffixes.add(words);
                break;
            }
        }
        fillTable(s);
        fillTable(r);
    }


    private TimeWords unEvidWords;
    public boolean isEvidClosed() {
        for(TimeWords sPrefix:s){
            for(TimeWords suffix:suffixes){
                TimeWords words= TimeWordsUtil.concat(sPrefix,suffix);
                if(!s.contains(words) && !r.contains(words)){
                    unEvidWords = words;
                    return false;
                }
            }
        }
        return true;
    }

    public void makeEvidClosed(){
        r.add(unEvidWords);
        fillTable(r);
    }


    @Override
    public void refine(TimeWords ce){
        Set<TimeWords> prefixesSet = TimeWordsUtil.getAllPrefixes(ce);
        r.addAll(prefixesSet);
        fillTable(r);
        makePrapared();
    }

    @Override
    public void buildHypothesis() {
        List<Location> locationList = new ArrayList<>();
        List<Transition> transitionList = new ArrayList<>();

        Map<Row, Location> rowLocationMap = new HashMap<>();
        //根据s中的row来创建Location；
        Set<Row> rowSet = new HashSet<>();
        int id = 1;
        for(TimeWords sWords: s){
            Pair pair = new Pair(sWords, TimeWords.EMPTY_WORDS);
            Row row = getRow(sWords);
            if(!rowSet.contains(row)){
                rowSet.add(row);
                boolean init = getRow(sWords).equals(getRow(TimeWords.EMPTY_WORDS));
                boolean accepted = answers.get(pair)== true;
                Location location = new Location(id,getName()+id,init,accepted);
                locationList.add(location);
                rowLocationMap.put(row,location);
                id++;
            }
        }

        //根据观察表来创建Transition
        Set<TimeWord> lastWordSet = getLastWordSet();
        List<TimeWords> prefixList = getPrefixList();
        for(TimeWords prefix: prefixList){
            Row row1 = getRow(prefix);
            Location location1 = rowLocationMap.get(row1);
            for(TimeWord word:lastWordSet){
                TimeWords timeWords = TimeWordsUtil.concat(prefix,word);
                if(prefixList.contains(timeWords)){
                    Row row2 = getRow(timeWords);
                    Location location2 = rowLocationMap.get(row2);
                    String action = word.getAction();
                    TimeGuard timeGuard = new TimeGuard(word);
                    Transition transition = new Transition(location1,location2,timeGuard,action);
                    if(!transitionList.contains(transition)){
                        transitionList.add(transition);
                    }
                }
            }
        }

        RTA evidenceRTA = new RTA(getName(),getSigma(),locationList,transitionList);

        RTA hypothesis = RTABuilder.evidToRTA(evidenceRTA);
        setHypothesis(hypothesis);
    }


    @Override
    public void show(){
        List<String> stringList = new ArrayList<>();
        List<String> suffixStringList = new ArrayList<>();
        List<TimeWords> prefixList = getPrefixList();
        int maxLen = 0;
        for(TimeWords words:prefixList){
            String s = words.toString();
            stringList.add(s);
            maxLen = maxLen > s.length()?maxLen:s.length();
        }
        for(TimeWords words:suffixes){
            String s = words.toString();
            suffixStringList.add(s);
        }


        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < maxLen; i++){
            sb.append(" ");
        }
        sb.append("|");
        for(String s:suffixStringList){
            sb.append(s);
            sb.append("|");
        }
        sb.append("\n");

        for(int i = 0; i < prefixList.size(); i++){
            String prefixString = stringList.get(i);
            sb.append(prefixString);
            int slen = s.size();
            for(int k = 0; k < maxLen-prefixString.length(); k++){
                sb.append(" ");
            }
            sb.append("|");
            for(int j = 0; j < suffixes.size(); j++){
                Pair pair = new Pair(prefixList.get(i),suffixes.get(j));
                boolean b = answers.get(pair);
                String a = b==true?"+":"-";
                sb.append(a);
                String suffixString = suffixStringList.get(j);
                for(int k = 0; k < suffixString.length()-1;k++){
                    sb.append(" ");
                }
                sb.append("|");
            }
            sb.append("\n");

            if(i == slen-1){
                for(int k = 0; k < maxLen; k++){
                    sb.append("-");
                }
                sb.append("|");
                for(String suffixString : suffixStringList){
                    for(int k = 0; k < suffixString.length();k++){
                        sb.append("-");
                    }
                    sb.append("|");
                }
                sb.append("\n");
            }
        }
        System.out.println(sb);
    }



    private Set<TimeWord> getLastWordSet(){
        Set<TimeWords> sr = getPrefixSet();
        Set<TimeWord> lastWordSet = new HashSet<>();
        for(TimeWords timeWords:sr){
            if(!timeWords.equals(TimeWords.EMPTY_WORDS)){
                int size = timeWords.size();
                TimeWords last = timeWords.subWords(size-1,size);
                lastWordSet.add(last.get(0));
            }
        }
        return lastWordSet;
    }

    private Set<TimeWords> getPrefixSet(){
        Set<TimeWords> sr = new HashSet<>();
        sr.addAll(s);
        sr.addAll(r);
        return sr;
    }

    private List<TimeWords> getPrefixList(){
        List<TimeWords> sr = new ArrayList<>();
        sr.addAll(s);
        sr.addAll(r);
        return sr;
    }

}
