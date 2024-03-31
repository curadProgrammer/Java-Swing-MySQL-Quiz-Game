import database.Category;
import screens.QuizScreenGui;
import screens.TitleScreenGui;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        // ensures Swing GUI tasks are executed on the event dispatch thread (EDT)
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                // create and display the title screen gui window
                new TitleScreenGui().setVisible(true);
            }
        });
    }
}
