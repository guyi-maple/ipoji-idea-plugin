package top.guyi.ipojo.idea.plugin.element.entry;

import java.util.Arrays;

/**
 * @author guyi
 * 节点属性
 */
public enum ElementType {

    STRING_VALUE("STRING_LITERAL",""),
    PROPERTY("PROPERTY","属性"),
    OBJECT("OBJECT","对象");

    private final String value;
    private final String text;

    ElementType(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public static ElementType getByValue(String value){
        return Arrays.stream(values())
                .filter(type -> type.value.equals(value))
                .findFirst()
                .orElse(null);
    }

}
