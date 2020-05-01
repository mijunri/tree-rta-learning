package rta;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import words.TimeWord;
import words.TimeWords;

import java.util.*;

public class RTA {

    private String name;
    private Set<String> sigma;
    private List<Location> locationList;
    private List<Transition> transitionList;


    public RTA(String name, Set<String> sigma, List<Location> locationList, List<Transition> transitionList) {
        this.name = name;
        this.sigma = sigma;
        this.locationList = locationList;
        this.transitionList = transitionList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getSigma() {
        return sigma;
    }

    public void setSigma(Set<String> sigma) {
        this.sigma = sigma;
    }

    public List<Location> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<Location> locationList) {
        this.locationList = locationList;
    }

    public List<Transition> getTransitionList() {
        return transitionList;
    }

    public void setTransitionList(List<Transition> transitionList) {
        this.transitionList = transitionList;
    }

    public int indexof(Location location){
        for(int i = 0; i < locationList.size(); i ++){
            if(locationList.get(i) == location){
                return i;
            }
        }
        return -1;
    }

    public Location getLocation(int id){
        for(Location location:locationList){
            if(location.getId()==id){
                return location;
            }
        }
        return null;
    }


    public List<Location> getAcceptedLocations(){
        List<Location> list = new ArrayList<>();
        for(Location l:locationList){
            if(l.isAccept()){
                list.add(l);
            }
        }
        return list;
    }

    public Location getInitLocation(){
        for(Location l:locationList){
            if(l.isInit()){
                return l;
            }
        }
        return null;
    }


    public List<Transition> getTransitions(Location fromLocation, String action, Location toLocation){
        List<Transition> list = new ArrayList<>(transitionList);
        if(fromLocation != null){
            Iterator<Transition> iterator = list.iterator();
            while(iterator.hasNext()){
                Transition t = iterator.next();
                int tSourceId  = t.getSourceId();
                int fromId = fromLocation.getId();
                if(tSourceId != fromId){
                    iterator.remove();
                }
            }
        }

        if(action != null){
            Iterator<Transition> iterator = list.iterator();
            while(iterator.hasNext()){
                Transition t = iterator.next();
                String tAction  = t.getAction();
                if(!tAction.equals(action)){
                    iterator.remove();
                }
            }
        }

        if(toLocation != null){
            Iterator<Transition> iterator = list.iterator();
            while(iterator.hasNext()){
                Transition t = iterator.next();
                int tTargetId  = t.getTargetId();
                int toId = toLocation.getId();
                if(tTargetId != toId){
                    iterator.remove();
                }
            }
        }
        return list;
    }

    public Location getLocation(TimeWords timeWords){
        Location location = getInitLocation();
        for(TimeWord w:timeWords.getWordList()){
            boolean flag = false;
            List<Transition> transitionList = getTransitions(location,null,null);
            for(Transition t:transitionList){
                if(t.isPass(w)){
                    location = t.getTargetLocation();
                    flag = true;
                    break;
                }
            }
            if(flag == false){
                return null;
            }
        }
        return location;
    }

    public boolean isAccepted(TimeWords timeWords){
        Location location = getLocation(timeWords);
        if(location == null || !location.isAccept()){
            return false;
        }
        return true;
    }


    @Override
    public String toString(){

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name",name);

        JSONArray stateArray = new JSONArray();
        for(Location l: locationList){
            stateArray.add(l.getId());
        }
        jsonObject.put("l",stateArray);

        JSONArray sigmaArray = new JSONArray();
        for(String s: sigma){
            sigmaArray.add(s);
        }
        jsonObject.put("sigma",sigmaArray);

        JSONObject tranJson = new JSONObject();
        for(int i = 0; i < transitionList.size();i++){
            JSONArray tranArray = new JSONArray();
            Transition t = transitionList.get(i);
            tranArray.add(t.getSourceId());
            tranArray.add(t.getAction());
            tranArray.add(t.getTimeGuard().toString());
            tranArray.add(t.getTargetId());
            tranJson.put(String.valueOf(i),tranArray);
        }
        jsonObject.put("tran",tranJson);

        Location initLocation = getInitLocation();
        int initId = initLocation.getId();
        jsonObject.put("init",initId);

        JSONArray acceptArray = new JSONArray();
        for(Location acceptLocation: getAcceptedLocations()){
            acceptArray.add(acceptLocation.getId());
        }
        jsonObject.put("accept",acceptArray);

        String str = jsonObject.toString();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str);

        String[] strings = new String[]{
                "\"sigma","\"init","\"name","\"tran","\"accept","\"l","}"
        };
        for(int i = 0; i < strings.length; i++){
            int index = stringBuilder.indexOf(strings[i]);
            stringBuilder.insert(index,"\n  ");
        }
        int index1 = stringBuilder.indexOf("]}");
        stringBuilder.insert(index1+1,"\n");


        for(int i = 0; i< transitionList.size();i++){
            int index = stringBuilder.indexOf("\""+i+"\"");
            stringBuilder.insert(index,"\n    ");
        }
        return stringBuilder.toString();
    }

    public RTA copy(){
        String name1 = name;
        Set<String> sigma1 = new HashSet<>(sigma);
        List<Location> locationList1 = new ArrayList<>();
        Map<Location,Location> locationMap = new HashMap<>();
        for(Location l:locationList){
            Location l1 = new Location(l.getId(),l.getName(),l.isInit(),l.isAccept());
            locationMap.put(l,l1);
            locationList1.add(l1);
        }
        List<Transition> transitionList1 = new ArrayList<>();
        for(Transition t:transitionList){
            Location source = locationMap.get(t.getTargetLocation());
            Location target = locationMap.get(t.getTargetLocation());
            TimeGuard guard = t.getTimeGuard().copy();
            Transition t1 = new Transition(source,target,guard,t.getAction());
            transitionList1.add(t1);
        }
        return new RTA(name1,sigma1,locationList1,transitionList1);
    }

    public int size(){
        return locationList.size();
    }
}
