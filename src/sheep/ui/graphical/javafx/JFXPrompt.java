package sheep.ui.graphical.javafx;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sheep.ui.Prompt;

import javax.swing.*;
import java.io.File;
import java.util.Optional;

public class JFXPrompt implements Prompt {
    private Stage stage;

    public JFXPrompt(Stage stage) {
        this.stage = stage;
    }

    @Override
    public Optional<String> ask(String prompt) {
        String result = JOptionPane.showInputDialog(prompt);
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<String[]> askMany(String[] prompts) {
        String[] responses = new String[prompts.length];
        for (int i = 0; i < prompts.length; i++) {
            responses[i] = JOptionPane.showInputDialog(prompts[i]);
            if (responses[i] == null) {
                return Optional.empty();
            }
        }
        return Optional.of(responses);
    }

    @Override
    public boolean askYesNo(String prompt) {
        int response = JOptionPane.showConfirmDialog(null, prompt, "Confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return response == JOptionPane.YES_OPTION;
    }

    @Override
    public void message(String prompt) {
        JOptionPane.showMessageDialog(null, prompt);
    }

    @Override
    public String openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Sheep files", "*.sheep"));

        try {
            File file = fileChooser.showOpenDialog(stage);
            return (file != null) ? file.getAbsolutePath() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String saveFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Sheep files", "*.sheep"));

        try {
            File file = fileChooser.showSaveDialog(stage);
            return (file != null) ? file.getAbsolutePath() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}