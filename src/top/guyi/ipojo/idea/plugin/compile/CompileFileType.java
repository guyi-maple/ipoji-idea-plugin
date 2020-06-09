package top.guyi.ipojo.idea.plugin.compile;

import com.intellij.json.JsonLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.guyi.ipojo.idea.plugin.Icons;

import javax.swing.*;

public class CompileFileType extends LanguageFileType {

    public static CompileFileType INSTANCE = new CompileFileType();

    protected CompileFileType() {
        super(JsonLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Ipojo Compile";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Ipojo compile";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return ".compile";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.COMPILE_INFO_ICON;
    }
}
