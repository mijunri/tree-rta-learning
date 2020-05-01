package rta;

import words.TimeWord;

public class Transition {

    public static final double THETA = 0.2;

    private Location sourceLocation;
    private Location targetLocation;
    private String action;
    private TimeGuard timeGuard;

    public Transition(Location sourceLocation, Location targetLocation, TimeGuard timeGuard, String action) {
        this.sourceLocation = sourceLocation;
        this.targetLocation = targetLocation;
        this.timeGuard = timeGuard;
        this.action = action;
    }

    public int getSourceId() {
        return sourceLocation.getId();
    }

    public int getTargetId() {
        return targetLocation.getId();
    }

    public String getSourceName() {
        return sourceLocation.getName();
    }


    public String getTargetName() {
        return targetLocation.getName();
    }


    public Location getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(Location sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(Location targetLocation) {
        this.targetLocation = targetLocation;
    }

    public TimeGuard getTimeGuard() {
        return timeGuard;
    }

    public void setTimeGuard(TimeGuard timeGuard) {
        this.timeGuard = timeGuard;
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isPass(TimeWord word){
        String action = word.getAction();
        double value = word.getValue();
        if(this.action.equals(action)){
            return timeGuard.isPass(value);
        }
        return false;
    }

    @Override
    public int hashCode(){
        return sourceLocation.hashCode()+targetLocation.hashCode()*2+action.hashCode()*3+timeGuard.hashCode()*4;
    }

    @Override
    public boolean equals(Object o){
        Transition transition = (Transition) o;
        if(sourceLocation == transition.sourceLocation && targetLocation == transition.targetLocation
            && action.equals(transition.action) && timeGuard.equals(transition.timeGuard)){
            return true;
        }
        return false;
    }


    public TimeWord toWord(){
        boolean leftOpen = timeGuard.isLeftOpen();
        int left = timeGuard.getLeft();
        if(!leftOpen){
            return new TimeWord(action,left);
        }
        else {
            return new TimeWord(action,left+THETA);
        }
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(sourceLocation.getId()).append(", ").append(action).append(",").append(timeGuard).append(", ")
                .append(targetLocation.getId()).append(")");
        return sb.toString();
    }
}

