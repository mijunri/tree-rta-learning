package rta;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import rta.Location;
import rta.RTA;
import rta.TimeGuard;
import rta.Transition;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class RTABuilder {

    public static RTA getRTAFromJsonFile(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
        String str = null;
        StringBuilder json = new StringBuilder();
        while ((str = reader.readLine()) != null){
            json.append(str);
        }
        return getRTAFromJson(json.toString());
    }

    public static RTA getRTAFromJson(String json){
        JSONObject jsonObject = JSON.parseObject(json);
        String name = jsonObject.getString("name");

        JSONArray jsonArray = jsonObject.getJSONArray("sigma");
        Set<String> sigma = new HashSet<>();
        Iterator<Object> iterator = jsonArray.iterator();
        while (iterator.hasNext()){
            sigma.add((String)iterator.next());
        }

        List<Location> locationList = new ArrayList<>();
        JSONArray locationArray = jsonObject.getJSONArray("s");
        List<Integer> list = new ArrayList<>();
        for(int i = 0; i < locationArray.size(); i ++){
            list.add(locationArray.getIntValue(i));
        }
        JSONArray acceptArray = jsonObject.getJSONArray("accept");
        Set<Integer> set = new HashSet<>();
        for(int i = 0; i < acceptArray.size(); i ++){
            set.add(acceptArray.getIntValue(i));
        }
        int initId = jsonObject.getInteger("init");
        for(int id:list){
            Location location = new Location(id);
            location.setName(""+id);
            if(set.contains(id)){
                location.setAccept(true);
            }else {
                location.setAccept(false);
            }
            if(id == initId){
                location.setInit(true);
            }else {
                location.setInit(false);
            }
            locationList.add(location);
        }

        Map<Integer, Location> map = new HashMap<>();
        for(Location l: locationList){
            map.put(l.getId(),l);
        }

        JSONObject tranJsonObject = jsonObject.getJSONObject("tran");

        int size = tranJsonObject.size();
        List<Transition> transitionList = new ArrayList<>();
        for(int i = 0; i < size; i++){
            JSONArray array = tranJsonObject.getJSONArray(String.valueOf(i));
            int sourceId = array.getInteger(0);
            Location sourceLocation =  map.get(sourceId);
            String action = array.getString(1);
            TimeGuard timeGuard = new TimeGuard(array.getString(2));
            int targetId = array.getInteger(3);
            Location targetLocation = map.get(targetId);
            Transition transition = new Transition(sourceLocation,targetLocation,timeGuard,action);
            transitionList.add(transition);
        }

        RTA rta =  new RTA(name,sigma,locationList,transitionList);

        return RTABuilder.completeRTA(rta);
    }



    public static RTA getNegtiveRTA(RTA rta){
        RTA neg = rta.copy();
        for(Location l:neg.getLocationList()){
            l.setAccept(!l.isAccept());
        }
        return neg;
    }

    public static RTA getCartesian(RTA r1, RTA r2){
        Set<String> sigma = new HashSet<>();
        sigma.addAll(r1.getSigma());
        sigma.addAll(r2.getSigma());

        Map<Integer,Location> map1 = new HashMap<>();
        Map<Integer,Location> map2 = new HashMap<>();

        for(Location l: r1.getLocationList()){
            map1.put(l.getId(),l);
        }
        for(Location l:r2.getLocationList()){
            map2.put(l.getId(),l);
        }

        List<Location> locationList = new ArrayList<>();
        for(Location l1:r1.getLocationList()){
            for(Location l2:r2.getLocationList()){

            }
        }


//        List<Location> locationList1 = r1.getLocationList();
//        List<Location> locationList2 = r2.getLocationList();
//        Set<String> sigma = new HashSet<>();
//        sigma.addAll(r1.getSigma());
//        sigma.addAll(r2.getSigma());
//        String name = r1.getName()+"-"+r2.getName();
//
//
//        //location的笛卡尔积
//        int len1 = locationList1.size();
//        int len2 = locationList2.size();
//        List<Location> locationList = new ArrayList<>();
//        for(int i = 0; i < len1; i ++){
//            Location location1 = locationList1.get(i);
//            for(int j = 0; j < len2; j ++){
//                Location location2 = locationList2.get(j);
//                int id = (i)*len2 + (j+1);
//                String locationName = location1.getName()+"-"+location2.getName();
//                boolean init = location1.isInit() && location2.isInit();
//                boolean accpted = location1.isAccept() && location2.isAccept();
//                Location location = new Location(id,locationName,init,accpted);
//                locationList.add(location);
//            }
//        }
//
//
//        //transition的笛卡尔积
//        List<Transition> transitionList = new ArrayList<>();
//
//        for(int i = 0; i < len1; i++){
//            for(int j = 0; j < len2; j++){
//                for(String action:sigma){
//                    Location location1 = locationList1.get(i);
//                    Location location2 = locationList2.get(j);
//                    List<Transition> transitionList1 = r1.getTransitions(location1,action,null);
//                    List<Transition> transitionList2 = r2.getTransitions(location2,action,null);
//                    int tranLen1 = transitionList1.size();
//                    int tranLen2 = transitionList2.size();
//                    Location newSourceLocation = locationList.get((i)*len2 + (j));
//
//                    for(int m = 0; m < tranLen1; m++){
//                        Transition tran1 = transitionList1.get(m);
//                        TimeGuard timeGuard1 = tran1.getTimeGuard();
//                        Location target1 = tran1.getTargetLocation();
//                        int index1 = r1.indexof(target1);
//
//                        for(int n = 0; n < tranLen2; n++){
//                            Transition tran2 = transitionList2.get(n);
//                            TimeGuard timeGuard2 = tran2.getTimeGuard();
//                            Location target2 = tran2.getTargetLocation();
//                            int index2 = r2.indexof(target2);
//                            Location newTargetLocation = locationList.get((index1)*len2 + (index2));
//                            TimeGuard timeGuard = timeGuard1.intersection(timeGuard2);
//                            if(timeGuard != null){
//                                Transition transition = new Transition(newSourceLocation,newTargetLocation,timeGuard,action);
//                                transitionList.add(transition);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        return new RTA(name,sigma,locationList,transitionList);
    }


    public static RTA completeRTA(RTA rta){
        Set<String> sigma = rta.getSigma();
        List<Location> oldLocationList = rta.getLocationList();
        List<Transition> oldTransitionList = rta.getTransitionList();
        Location sinkLocation = new Location(oldLocationList.size()+1,"sink",false,false);
        List<Location> locationList = new ArrayList<>();
        List<Transition> transitionList = new ArrayList<>();
        locationList.addAll(oldLocationList);
        locationList.add(sinkLocation);
        transitionList.addAll(oldTransitionList);
        for(String action:sigma){
            TimeGuard timeGuard = new TimeGuard(false,true,0,TimeGuard.MAX_TIME);
            Transition transition = new Transition(sinkLocation,sinkLocation,timeGuard,action);
            transitionList.add(transition);
        }
        for(Location l:oldLocationList){
            for(String action:sigma){
                List<Transition> transitionList1 = rta.getTransitions(l,action,null);
                TimeGuard timeGuard = new TimeGuard(false,true,0,0);
                Transition pre = new Transition(sinkLocation,sinkLocation,timeGuard,action);
                for(int i = 0; i < transitionList1.size(); i++){
                    Transition current = transitionList1.get(i);
                    TimeGuard preTimeGuard = pre.getTimeGuard();
                    TimeGuard currentTimeGuard = current.getTimeGuard();
                    boolean var0 = preTimeGuard.getRight() ==0 && currentTimeGuard.getLeft()==0;
                    boolean var1 = currentTimeGuard.getLeft()==preTimeGuard.getRight();
                    boolean var2 = currentTimeGuard.isLeftOpen();
                    boolean var3 = !preTimeGuard.isRightOpen();
                    if(var0 || (var1 && var2 && var3)){
                        pre = current;
                    }else {
                        boolean leftO = !preTimeGuard.isRightOpen();
                        boolean rightO = !currentTimeGuard.isLeftOpen();
                        int left = preTimeGuard.getRight();
                        int right = currentTimeGuard.getLeft();
                        TimeGuard timeGuard1 = new TimeGuard(leftO,rightO,left,right);
                        Transition t = new Transition(l,sinkLocation,timeGuard1,action);
                        transitionList.add(t);
                        pre = current;
                    }
                }
                TimeGuard preTimeGuard = pre.getTimeGuard();
                if(preTimeGuard.getRight()!=TimeGuard.MAX_TIME){
                    boolean leftO = !preTimeGuard.isRightOpen();
                    boolean rightO = false;
                    int left = preTimeGuard.getRight();
                    int right = TimeGuard.MAX_TIME;
                    TimeGuard timeGuard1 = new TimeGuard(leftO,rightO,left,right);
                    Transition t = new Transition(l,sinkLocation,timeGuard1,action);
                    transitionList.add(t);
                }
            }
        }
        RTA rta1 =    new RTA(rta.getName(),sigma,locationList,transitionList);
        List<Transition> toSinkTransitionList = rta1.getTransitions(null,null,sinkLocation);
        if(toSinkTransitionList.size() == sigma.size()){
            return rta;
        }
        return rta1;
    }
}
