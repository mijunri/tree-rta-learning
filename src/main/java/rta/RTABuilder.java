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
        Map<Integer,Location> map = new HashMap<>();
        for(Location l1:r1.getLocationList()){
            for(Location l2:r2.getLocationList()){
                String name = l1.getName()+":"+l2.getName();
                int id = (l2.getId()-1)*r1.size()+l1.getId();
                boolean init = l1.isInit() && l2.isInit();
                boolean accpted = l1.isAccept() && l2.isAccept();
                Location location = new Location(id,name,init,accpted);
                map.put(id,location);
                locationList.add(location);
            }
        }

        List<Transition> transitionList = new ArrayList<>();
        for(Transition t1:r1.getTransitionList()){
            for(Transition t2: r2.getTransitionList()){
                if(!t1.getAction().equals(t2.getAction())){
                    continue;
                }

                TimeGuard guard = t1.getTimeGuard().intersection(t2.getTimeGuard());
                if(guard == null){
                    continue;
                }

                int sourceId = (t2.getSourceId()-1)*r1.size()+t1.getSourceId();
                int targetId = (t2.getTargetId()-1)*r1.size()+t1.getTargetId();
                Location source = map.get(sourceId);
                Location target = map.get(targetId);
                Transition t = new Transition(map.get(sourceId),map.get(targetId),guard,t1.getAction());
                transitionList.add(t);
            }
        }
        String name = r1.getName()+":"+r2.getName();
        return new RTA(name,sigma,locationList,transitionList);
    }

    public static RTA completeRTA(RTA rta){

        RTA copy = rta.copy();
        Location sink = new Location(copy.size()+1,"sink",false,false);

        List<Transition> transitionList0 = new ArrayList<>();
        for(Location l: copy.getLocationList()){
            for(String action: copy.getSigma()){
                List<Transition> transitionList = rta.getTransitions(l,action,null);
                if(transitionList.isEmpty()){
                    TimeGuard guard = new TimeGuard(false,false,0,TimeGuard.MAX_TIME);
                    Transition t = new Transition(l,sink,guard,action);
                    transitionList0.add(t);
                }
                sortTran(transitionList);
                Transition t0 = transitionList.get(0);
                TimeGuard g0 = t0.getTimeGuard();
                if(g0.getLeft()!=0 || g0.isLeftOpen()){
                    TimeGuard guard = new TimeGuard(false,!g0.isLeftOpen(),0,g0.getLeft());
                    Transition t = new Transition(l,sink,guard,action);
                    transitionList0.add(t);
                }
                for(int i = 1; i < transitionList.size(); i++){
                    Transition t1 = transitionList.get(i);
                    TimeGuard g1 = t1.getTimeGuard();
                    if(g0.getRight()!= g1.getLeft() || (g0.isRightOpen() && g1.isLeftOpen())){
                        TimeGuard guard = new TimeGuard(!g0.isRightOpen(),!g1.isLeftOpen(),g0.getRight(),g1.getLeft());
                        Transition t = new Transition(l,sink,guard,action);
                        transitionList0.add(t);
                    }
                    t0 = t1;
                    g0 = t0.getTimeGuard();
                }
                g0 = t0.getTimeGuard();
                if(g0.getRight()!=TimeGuard.MAX_TIME ){
                    TimeGuard guard = new TimeGuard(!g0.isRightOpen(),false,g0.getRight(),TimeGuard.MAX_TIME);
                    Transition t = new Transition(l,sink,guard,action);
                    transitionList0.add(t);
                }
            }
        }
        if(transitionList0.isEmpty()){
            return copy;
        }else {
            for(String action:copy.getSigma()){
                TimeGuard timeGuard = new TimeGuard(false,false,0,TimeGuard.MAX_TIME);
                Transition transition = new Transition(sink,sink,timeGuard,action);
                transitionList0.add(transition);
            }
            copy.getLocationList().add(sink);
            copy.getTransitionList().addAll(transitionList0);
            return copy;
        }

    }

    public static void sortTran(List<Transition> transitionList){
        transitionList.sort(new Comparator<Transition>() {
            @Override
            public int compare(Transition o1, Transition o2) {
                if(o1.getSourceId() != o2.getSourceId()){
                    return o1.getSourceId() - o2.getSourceId();
                }
                if(o1.getAction().compareTo(o2.getAction())!= 0 ){
                    return o1.getAction().compareTo(o2.getAction());
                }
                if(o1.getTimeGuard().getLeft() != o2.getTimeGuard().getLeft()){
                    return o1.getTimeGuard().getLeft() - o2.getTimeGuard().getLeft();
                }

                if(o1.getTimeGuard().isLeftOpen() != o2.getTimeGuard().isLeftOpen()){
                    return o1.getTimeGuard().isLeftOpen()?1:-1;
                }
                return 1;
            }
        });
    }

    public static RTA evidToRTA(RTA evidenceRTA){
        for(Location l:evidenceRTA.getLocationList()){
            for(String action: evidenceRTA.getSigma()){
                List<Transition> transitionList1 = evidenceRTA.getTransitions(l,action,null);
                transitionList1.sort(new Comparator<Transition>() {
                    @Override
                    public int compare(Transition o1, Transition o2) {
                        if(o1.getTimeGuard().getLeft() < o2.getTimeGuard().getLeft()){
                            return -1;
                        }
                        if(o1.getTimeGuard().getLeft() == o2.getTimeGuard().getLeft()
                                && !o1.getTimeGuard().isLeftOpen()){
                            return -1;
                        }
                        return 1;
                    }
                });
                for(int i = 0; i < transitionList1.size(); i++){
                    if(i < transitionList1.size()-1){
                        TimeGuard timeGuard1 = transitionList1.get(i).getTimeGuard();
                        TimeGuard timeGuard2 = transitionList1.get(i+1).getTimeGuard();
                        timeGuard1.setRight(timeGuard2.getLeft());
                        timeGuard1.setRightOpen(!timeGuard2.isLeftOpen());
                    }else {
                        TimeGuard timeGuard1 = transitionList1.get(i).getTimeGuard();
                        timeGuard1.setRight(TimeGuard.MAX_TIME);
                        timeGuard1.setRightOpen(false);
                    }
                }
            }
        }
        return evidenceRTA;
    }
}
