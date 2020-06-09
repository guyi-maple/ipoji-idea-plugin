package top.guyi.ipojo.idea.plugin.actions.template;

import com.google.gson.annotations.SerializedName;

public class TemplateParameter {

    private String name;
    private String text;
    @SerializedName("default")
    private String defaultValue;
    private String emptyMessage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getEmptyMessage() {
        return emptyMessage;
    }

    public void setEmptyMessage(String emptyMessage) {
        this.emptyMessage = emptyMessage;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
