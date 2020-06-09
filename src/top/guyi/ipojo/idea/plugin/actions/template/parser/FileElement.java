package top.guyi.ipojo.idea.plugin.actions.template.parser;

import top.guyi.ipojo.idea.plugin.actions.template.change.ChangeItemValue;

import java.util.List;
import java.util.Map;

public interface FileElement {

    boolean isCreate();
    String getName();
    String getValue();

    default void setValue(String key,String value){
        if ("this".equals(key)){
            this.setValue(value);
        }else{
            this.findChildren(key)
                    .forEach(e -> e.setValue(value));
        }
    }

    default void setValue(Map<String,String> values){
        values.forEach(this::setValue);
    }

    void setValue(String value);

    List<FileElement> findChildren(String elementName);

    void addElement(String key,String value);

}
