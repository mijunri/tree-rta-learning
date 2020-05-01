package experiment;

import lstar.Membership;
import lstar.RTAMembership;
import rta.RTA;
import rta.RTABuilder;

import java.io.IOException;

public class Experiment {
    public static void main(String[] args) throws IOException {
        String base = ".\\src\\main\\resources\\rtaJson\\3_3_3\\3_3_3-";
        for(int i = 0; i < 20; i++){
            String path = base+i+".json";
            RTA rta = RTABuilder.getRTAFromJsonFile(path);
            Membership membership = new RTAMembership(rta);

        }
    }
}
