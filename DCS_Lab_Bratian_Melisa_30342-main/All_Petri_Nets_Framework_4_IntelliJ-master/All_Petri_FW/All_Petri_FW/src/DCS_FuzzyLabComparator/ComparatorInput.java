package DCS_FuzzyLabComparator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class ComparatorInput {

    public static void main(String[] args)
            throws InterruptedException, IOException {

        File file = new File("D:\\PetriInputData\\comparator.txt");
        file.getParentFile().mkdirs();
        Files.deleteIfExists(file.toPath());

        FileWriter fw = new FileWriter(file.getPath());

        for (float i = 0.0F; i < 100.0F; ++i) {

            Float f1 = (float) Math.sin(Math.toRadians(i));
            Float f2 = (float) Math.cos(Math.toRadians(i));

            fw.write("P0:" + f1 + "F,P1:" + f2 + "F\n");
        }

        fw.close();
        System.out.println("Done!");
    }
}
