package experiment;

import lstar.*;
import observationTable.ObservationTable;
import rta.RTA;
import rta.RTABuilder;
import ttt.DTree;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Experiment {
    public static void main(String[] args) throws IOException {
        String base = ".\\src\\main\\resources\\rtaJson\\7_4_2\\";
        double costTime1 = 0;
        double tranSize = 0;
        double membershipCount1 = 0;
        double equivalenceCount1 = 0;

        double costTime2 = 0;
        double membershipCount2 = 0;
        double equivalenceCount2 = 0;

        for(int i = 1; i <= 20; i++){
            String path = base+"7_4_2-"+i+".json";
            RTA rta = RTABuilder.getRTAFromJsonFile(path);
            Membership membership1 = new RTAMembership(rta);
            Membership membership2 = new RTAMembership(rta);
            EquivalenceQuery equivalenceQuery1 = new RTAEquivalenceQuery(rta);
            EquivalenceQuery equivalenceQuery2 = new RTAEquivalenceQuery(rta);
            LearningMethod observationTable = new ObservationTable("hypothesis1",rta.getSigma(),membership1,equivalenceQuery1);
            LearningMethod dTree = new DTree("hypothesis2",rta.getSigma(),membership2,equivalenceQuery2);
            Learner observationTableLearner = new Learner(membership1,equivalenceQuery1,observationTable);
            Learner tttLearner = new Learner(membership2,equivalenceQuery2,dTree);
            observationTableLearner.doExperiment();
            tttLearner.doExperiment();

            System.out.println(rta.getName()+"-"+i);
            System.out.println("observationTable:");
            System.out.println("cost:"+observationTableLearner.getCostTime());
            System.out.println("membership:"+observationTableLearner.getMembershipCount());
            System.out.println("equivalence:"+observationTableLearner.getEquivalenceQueryCount());
            System.out.println("dTree:");
            System.out.println("cost:"+tttLearner.getCostTime());
            System.out.println("membership:"+tttLearner.getMembershipCount());
            System.out.println("equivalence:"+tttLearner.getEquivalenceQueryCount());
            System.out.println("**********************************");
            costTime1 += observationTableLearner.getCostTime();
            costTime2 += tttLearner.getCostTime();
            tranSize += rta.getTransitionList().size();
            membershipCount1+=observationTableLearner.getMembershipCount();
            membershipCount2+=tttLearner.getMembershipCount();
            equivalenceCount1+=observationTableLearner.getEquivalenceQueryCount();
            equivalenceCount2+=tttLearner.getEquivalenceQueryCount();
        }
        costTime1 /= 20;
        costTime2 /= 20;
        membershipCount1 /= 20;
        membershipCount2 /= 20;
        equivalenceCount1 /= 20;
        equivalenceCount2 /= 20;
        tranSize /= 20;
        StringBuilder sb = new StringBuilder();
        sb.append("the mean number of transition is:").append(tranSize).append("\n")
                .append("observationTable:: \n\tcostTime:").append(costTime1).append(",\n")
                .append("\tmembership:").append(membershipCount1).append(",\n")
                .append("\tequivalence:").append(equivalenceCount1).append("\n")
                .append("Dtree:: \n\tcostTime:").append(costTime2).append(",\n")
                .append("\tmembership:").append(membershipCount2).append(",\n")
                .append("\tequivalence:").append(equivalenceCount2).append("\n");
        String resultPath = base+"result\\result.txt";

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultPath)));
        bw.write(sb.toString());
        bw.close();
    }
}
