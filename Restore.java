import tme3.Fixable;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Restore { //Created Step4 Part5
    private String filename;

    public Restore(String filename) {
        this.filename = filename;
    }

    public void restore() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            GreenhouseControls gc = (GreenhouseControls) in.readObject();
            System.out.println("Restored GreenhouseControls: " + gc);
            Fixable fixable = gc.getFixable(gc.getError());
            if (fixable != null) {
                fixable.fix();
                fixable.log();
            }
            gc.run();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error restoring GreenhouseControls: " + e);
        }
    }
}