package top.guyi.ipojo.idea.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import top.guyi.ipojo.idea.plugin.actions.dialog.TemplateDialog;


public class InitializationAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        if (project != null){
            TemplateDialog dialog = new TemplateDialog(project);
            dialog.setTitle(TemplateDialog.TITLE);
            dialog.show();
        }
    }
}
