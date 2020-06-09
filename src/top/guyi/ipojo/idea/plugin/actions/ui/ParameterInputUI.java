package top.guyi.ipojo.idea.plugin.actions.ui;

import javax.swing.*;

public class ParameterInputUI {

    private JTextField text;
    private JLabel label;
    private JPanel line;

    public JPanel getLine() {
        return line;
    }

    public void setLabel(String label){
        this.label.setText(label);
    }

    public String getValue(){
        return this.text.getText();
    }

    public void setValue(String value){
        this.text.setText(value);
    }

}
