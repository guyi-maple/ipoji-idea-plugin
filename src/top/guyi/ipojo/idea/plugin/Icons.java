package top.guyi.ipojo.idea.plugin;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class Icons {

    public static Icon COMPILE_INFO_ICON = IconLoader.getIcon("/icons/compile_file_type.png");
    public static Icon ATTACH_ICON = IconLoader.getIcon("/icons/attach_file_type.png");
    public static Icon CONFIGURATION_ICON = IconLoader.getIcon("/icons/configuration_file_type.png");
    public static Icon FELIX_ICON = IconLoader.getIcon("/icons/felix_file_type.png");

    private final static Map<String,Icon> icons = new HashMap<>();

    static {
        icons.put("compile",COMPILE_INFO_ICON);
        icons.put("attach",ATTACH_ICON);
        icons.put("configuration",CONFIGURATION_ICON);
        icons.put("felix",FELIX_ICON);
    }

    public static Icon get(String name, Icon defaultIcon){
        return icons.getOrDefault(name,defaultIcon);
    }

}
