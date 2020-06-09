package top.guyi.ipojo.idea.plugin.actions.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.Nullable;
import top.guyi.ipojo.idea.plugin.actions.template.ProjectTemplate;
import top.guyi.ipojo.idea.plugin.actions.template.TemplateParameter;
import top.guyi.ipojo.idea.plugin.actions.ui.ParameterInputUI;

import javax.swing.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class ParameterWrapper {

    private TemplateParameter parameter;
    private ParameterInputUI ui;

    public ParameterWrapper(TemplateParameter parameter, ParameterInputUI ui) {
        this.parameter = parameter;
        this.ui = ui;
        this.ui.setLabel(this.parameter.getText());
        if (parameter.getDefaultValue() != null){
            ui.setValue(parameter.getDefaultValue());
        }
    }

    public TemplateParameter getParameter() {
        return parameter;
    }

    public ParameterInputUI getUi() {
        return ui;
    }
}

public class BuildDialog extends DialogWrapper {

    public static final String TITLE = "初始化项目";

    private Project project;
    private ProjectTemplate template;
    private List<ParameterWrapper> wrappers;

    public BuildDialog(@Nullable Project project, ProjectTemplate template) {
        super(project);
        this.project = project;
        this.template = template;
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel();
        BoxLayout layout = new BoxLayout(panel,BoxLayout.Y_AXIS);
        panel.setLayout(layout);
        if (this.template.getParameters().isEmpty()){
            panel.add(new JLabel("无模板参数"));
            this.wrappers = Collections.emptyList();
        }else{
            this.wrappers = this.template.getParameters()
                    .stream()
                    .map(p -> new ParameterWrapper(p,new ParameterInputUI()))
                    .peek(wrapper -> panel.add(wrapper.getUi().getLine()))
                    .collect(Collectors.toList());
        }
        return panel;
    }

    @Override
    protected void doOKAction() {
        Map<String,String> parameters = new HashMap<>();
        for (ParameterWrapper wrapper : this.wrappers) {
            String value = wrapper.getUi().getValue().trim();
            if ("".equals(value)){
                Messages.showMessageDialog(
                        project,
                        wrapper.getParameter().getEmptyMessage() == null ?
                                String.format("%s不能为空",wrapper.getParameter().getText()) : wrapper.getParameter().getEmptyMessage(),
                        "输入错误",
                        Messages.getErrorIcon()
                );
                return;
            }
            parameters.put(
                    wrapper.getParameter().getName(),
                    value
            );
        }

        parameters.put("basedir",project.getBasePath());

        this.getCancelAction().setEnabled(false);
        this.getOKAction().setEnabled(false);
        this.setTitle("项目生成中...");

        try {
            template.execute(project.getBasePath(),parameters);
            Messages.showMessageDialog(
                    project,
                    "项目成功初始化为Ipojo项目,记得刷新项目文件",
                    "项目生成完成",
                    Messages.getErrorIcon()
            );
            this.close(0);
        } catch (Exception e) {
            e.printStackTrace();
            this.getCancelAction().setEnabled(true);
            this.getOKAction().setEnabled(true);
            this.setTitle(TITLE);
            Messages.showMessageDialog(
                    project,
                    e.getMessage(),
                    "项目生成失败",
                    Messages.getErrorIcon()
            );
        }
    }
}
