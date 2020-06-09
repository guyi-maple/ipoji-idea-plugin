package top.guyi.ipojo.idea.plugin.element;

import top.guyi.ipojo.idea.plugin.element.handler.ElementHandlerEntry;

import java.util.List;

public class Element {

    private String value;
    private String text;
    private String icon;
    private List<Element> children;
    private ElementHandlerEntry handler;
    private List<Element> defaults;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Element> getChildren() {
        return children;
    }

    public void setChildren(List<Element> children) {
        this.children = children;
    }

    public ElementHandlerEntry getHandler() {
        return handler;
    }

    public void setHandler(ElementHandlerEntry handler) {
        this.handler = handler;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<Element> getDefaults() {
        return defaults;
    }

    public void setDefaults(List<Element> defaults) {
        this.defaults = defaults;
    }
}
