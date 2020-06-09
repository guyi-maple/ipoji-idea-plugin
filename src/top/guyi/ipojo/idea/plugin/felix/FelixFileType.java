package top.guyi.ipojo.idea.plugin.felix;

import com.intellij.json.JsonLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.guyi.ipojo.idea.plugin.Icons;

import javax.swing.*;

public class FelixFileType extends LanguageFileType {

    public static FelixFileType INSTANCE = new FelixFileType();

    protected FelixFileType() {
        super(JsonLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Felix Configuration";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Felix configuration file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return ".felix";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.FELIX_ICON;
    }
}
