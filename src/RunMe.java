import core.DisplayWindow;

import java.io.File;

public class RunMe {
    public static void main(String[] args) {
        File file = new File("ballCenters.txt");
        if(file.exists()) {
            System.out.println(file.delete() ? "ballCenters.txt deleted" : "Error deleting ballCenters.txt");
        }
        // --== Load an image to filter ==--
//        DisplayWindow.showFor("images/red.jpg", 1000, 900);
//        DisplayWindow.showFor("images/reds.jpg", 1000, 900);
//        DisplayWindow.showFor("images/allcolors.jpg", 1300, 900);
//        DisplayWindow.showFor("images/four.jpg", 1000, 1200);
//        DisplayWindow.showFor("images/five.jpg", 800, 1000);
//        DisplayWindow.showFor("images/realballs.jpg", 1300, 900);
        // --== Determine your input interactively with menus ==--
//        DisplayWindow.getInputInteractively(800,600);
    }
}