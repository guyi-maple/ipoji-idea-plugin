package top.guyi.ipojo.idea.plugin.attach;

import com.intellij.json.JsonLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.guyi.ipojo.idea.plugin.Icons;

import javax.swing.*;

public class AttachFileType extends LanguageFileType {

    public static AttachFileType INSTANCE = new AttachFileType();

    protected AttachFileType() {
        super(JsonLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Ipojo Attach";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Ipojo attach file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return ".attach";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.ATTACH_ICON;
    }
}
