import core.DisplayWindow;

import java.io.File;

public class RunMe {
    public static void main(String[] args) {
        File file = new File("ballCenters.txt");
        if(file.exists()) {
            System.out.println(file.delete() ? "File deleted" : "Error deleting file");
        }
        // --== Load an image to filter ==--
//        DisplayWindow.showFor("images/redblueyellowgreen.jpeg", 1300, 700);
        // --== Determine your input interactively with menus ==--
        DisplayWindow.getInputInteractively(800,600);
    }
}