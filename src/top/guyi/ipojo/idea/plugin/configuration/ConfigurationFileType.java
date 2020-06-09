package top.guyi.ipojo.idea.plugin.configuration;

import com.intellij.json.JsonLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.guyi.ipojo.idea.plugin.Icons;

import javax.swing.*;

public class ConfigurationFileType extends LanguageFileType {

    public static ConfigurationFileType INSTANCE = new ConfigurationFileType();

    protected ConfigurationFileType() {
        super(JsonLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Ipojo Configuration";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Ipojo configuration file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return ".configuration";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.CONFIGURATION_ICON;
    }
}
