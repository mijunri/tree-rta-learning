package observationTable;

import words.TimeWords;
import words.TimeWordsUtil;

public class Pair {
    private TimeWords prefix;
    private TimeWords suffix;

    public Pair(TimeWords prefix, TimeWords suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public TimeWords getPrefix() {
        return prefix;
    }

    public void setPrefix(TimeWords prefix) {
        this.prefix = prefix;
    }

    public TimeWords getSuffix() {
        return suffix;
    }

    public void setSuffix(TimeWords suffix) {
        this.suffix = suffix;
    }

    @Override
    public int hashCode(){
        return prefix.hashCode()*2 + suffix.hashCode();
    }

    @Override
    public boolean equals(Object o){
        Pair pair = (Pair)o;
        if(this.prefix.equals(pair.prefix) && this.suffix.equals(pair.suffix)){
            return true;
        }
        return false;
    }

    public TimeWords timeWords(){
        return TimeWordsUtil.concat(prefix,suffix);
    }

    @Override
    public String toString(){
        String s = prefix.toString()+"::"+suffix.toString();
        return s;
    }

}
