package DCS_FuzzyLab.RoomHeatingSystem;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import Components.Activation;
import Components.Condition;
import Components.GuardMapping;
import Components.PetriNet;
import Components.PetriNetWindow;
import Components.PetriTransition;
import DataObjects.DataFuzzy;
import DataObjects.DataTransfer;
import DataOnly.FLRS;
import DataOnly.FV;
import DataOnly.PlaceNameWithWeight;
import DataOnly.TransferOperation;
import Enumerations.FZ;
import Enumerations.TransitionCondition;
import Enumerations.TransitionOperation;

public class ORC {
    public static void main(String[] args) throws FileNotFoundException {
        PetriNet pn = new PetriNet();
        pn.PetriNetName = "ORC";
        pn.NetworkPort = 1084; // HTC uses 1083
        pn.SetInputFile("D:\\PetriInputData\\ORC.txt");

        // Copy the outside temperature to two parallel paths.
        FLRS copyTwo = new FLRS(
                new FV(FZ.NL, FZ.NL), new FV(FZ.NM, FZ.NM),
                new FV(FZ.ZR, FZ.ZR), new FV(FZ.PM, FZ.PM),
                new FV(FZ.PL, FZ.PL));
        FLRS copyOne = new FLRS(
                new FV(FZ.NL), new FV(FZ.NM), new FV(FZ.ZR),
                new FV(FZ.PM), new FV(FZ.PL));

        // Outside temperature: NL NM ZR PM PL
        // Water reference:      PL PM ZR NM NL
        FLRS inverse = new FLRS(
                new FV(FZ.PL), new FV(FZ.PM), new FV(FZ.ZR),
                new FV(FZ.NM), new FV(FZ.NL));

        for (int i = 0; i <= 5; i++) {
            DataFuzzy place = new DataFuzzy();
            place.SetName("P" + i);
            pn.PlaceList.add(place);
        }

        DataTransfer toHTC = new DataTransfer();
        toHTC.SetName("toHTC");
        toHTC.Value = new TransferOperation("localhost", "1083", "P1");
        pn.PlaceList.add(toHTC);

        // t0: read outside temperature from P1, branch to P2 and P3
        PetriTransition t0 = new PetriTransition(pn);
        t0.TransitionName = "t0";
        t0.InputPlaceName.add("P1");
        GuardMapping g0 = new GuardMapping();
        g0.condition = new Condition(t0, "P1", TransitionCondition.NotNull);
        ArrayList<PlaceNameWithWeight> in0 = new ArrayList<>();
        in0.add(new PlaceNameWithWeight("P1", 1F));
        ArrayList<String> out0 = new ArrayList<>();
        out0.add("P2");
        out0.add("P3");
        g0.Activations.add(new Activation(t0, copyTwo, in0, TransitionOperation.FLRS, out0));
        t0.GuardMappingList.add(g0);
        t0.Delay = 0;
        pn.Transitions.add(t0);

        // t1: feedback path shown in the diagram
        PetriTransition t1 = new PetriTransition(pn);
        t1.TransitionName = "t1";
        t1.InputPlaceName.add("P2");
        GuardMapping g1 = new GuardMapping();
        g1.condition = new Condition(t1, "P2", TransitionCondition.NotNull);
        ArrayList<PlaceNameWithWeight> in1 = new ArrayList<>();
        in1.add(new PlaceNameWithWeight("P2", 1F));
        ArrayList<String> out1 = new ArrayList<>();
        out1.add("P0");
        g1.Activations.add(new Activation(t1, copyOne, in1, TransitionOperation.FLRS, out1));
        t1.GuardMappingList.add(g1);
        t1.Delay = 0;
        pn.Transitions.add(t1);

        // t2: fuzzy mapping from outside temperature to water reference
        PetriTransition t2 = new PetriTransition(pn);
        t2.TransitionName = "t2";
        t2.InputPlaceName.add("P3");
        GuardMapping g2 = new GuardMapping();
        g2.condition = new Condition(t2, "P3", TransitionCondition.NotNull);
        ArrayList<PlaceNameWithWeight> in2 = new ArrayList<>();
        in2.add(new PlaceNameWithWeight("P3", 1F));
        ArrayList<String> out2 = new ArrayList<>();
        out2.add("P4");
        g2.Activations.add(new Activation(t2, inverse, in2, TransitionOperation.FLRS, out2));
        t2.GuardMappingList.add(g2);
        t2.Delay = 0;
        pn.Transitions.add(t2);

        // t3: send computed water reference to HTC.P1; also show it in P5
        PetriTransition t3 = new PetriTransition(pn);
        t3.TransitionName = "t3";
        t3.InputPlaceName.add("P4");
        GuardMapping g3 = new GuardMapping();
        g3.condition = new Condition(t3, "P4", TransitionCondition.NotNull);
        g3.Activations.add(new Activation(t3, "P4", TransitionOperation.SendOverNetwork, "toHTC"));
        g3.Activations.add(new Activation(t3, "P4", TransitionOperation.Move, "P5"));
        t3.GuardMappingList.add(g3);
        t3.Delay = 0;
        pn.Transitions.add(t3);

        System.out.println("ORC started (port 1084)");
        pn.Delay = 500;
        pn.ShowLogInWindow = true;
        PetriNetWindow frame = new PetriNetWindow(false);
        frame.petriNet = pn;
        frame.setVisible(true);
    }
}
