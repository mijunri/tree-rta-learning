package rta;

import org.junit.Test;

import java.sql.Time;

import static org.junit.Assert.*;

public class TimeGuardTest {

    @Test
    public void intersection() {
        TimeGuard guard1 = new TimeGuard("(3,5)");
        TimeGuard guard2 = new TimeGuard("(2,4)");
        TimeGuard guard3 = new TimeGuard("(2,3)");
        TimeGuard guard4 = new TimeGuard("(1,3]");
        TimeGuard guard5 = new TimeGuard("(0,3]");
        TimeGuard guard6 = new TimeGuard("[0,0]");

        System.out.println(guard1.intersection(guard2));
        System.out.println(guard1.intersection(guard3));
        System.out.println(guard1.intersection(guard4));
        System.out.println(guard4.intersection(guard5));
        System.out.println(guard6.intersection(guard6));

    }
}