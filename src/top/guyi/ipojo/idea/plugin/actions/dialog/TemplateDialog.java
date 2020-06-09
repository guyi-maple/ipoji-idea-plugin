package top.guyi.ipojo.idea.plugin.actions.dialog;

import com.google.gson.GsonBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jetbrains.annotations.Nullable;
import top.guyi.ipojo.idea.plugin.actions.template.ProjectTemplate;
import top.guyi.ipojo.idea.plugin.actions.ui.TemplateInputUI;

import javax.swing.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class TemplateDialog extends DialogWrapper {

    public static final String TITLE = "输入模板文件URL";

    private final Project project;
    private final TemplateInputUI ui;

    public TemplateDialog(@Nullable Project project) {
        super(project);
        this.project = project;
        this.ui = new TemplateInputUI();
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return ui.getUi();
    }

    @Override
    protected void doOKAction() {
        String template = ui.getTemplate().getText().trim();
        if ("".equals(template)){
            Messages.showMessageDialog(project,"请填写模板文件URL","输入错误", Messages.getErrorIcon());
            return;
        }

        URL url;
        try {
            url = new URL(template);
        } catch (MalformedURLException e) {
            Messages.showMessageDialog(project,e.getMessage(),"输入错误", Messages.getErrorIcon());
            return;
        }

        this.getOKAction().setEnabled(false);
        this.getCancelAction().setEnabled(false);
        this.setTitle("加载模板文件...");

        try {
            ProjectTemplate projectTemplate = ProjectTemplate.getInstance(url.openStream());
            BuildDialog dialog = new BuildDialog(project,projectTemplate);
            dialog.setTitle(BuildDialog.TITLE);
            dialog.show();
            this.close(0);
        } catch (Exception e) {
            Messages.showMessageDialog(project,e.getMessage(),"模板文件加载失败", Messages.getErrorIcon());
            this.getOKAction().setEnabled(true);
            this.getCancelAction().setEnabled(true);
            this.setTitle(TITLE);
        }


    }

    @Override
    public void doCancelAction() {
        this.close(0);
    }

    private boolean build(){
        String packageName = "";
        if (packageName == null || "".equals(packageName)){
            Messages.showMessageDialog(project,"项目根包不能为空","初始化失败", Messages.getErrorIcon());
            return false;
        }

        String basedir = project.getBasePath();

        String pomPath = String.format("%s/pom.xml",basedir);
        if (Files.notExists(Paths.get(pomPath))){
            Messages.showMessageDialog(project,"Ipojo项目必须是一个Maven项目","初始化失败", Messages.getErrorIcon());
            return false;
        }

        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new File(pomPath));
            Element root = document.getRootElement();

            String compilePath = String.format("%s/ipojo.compile",basedir);
            if (Files.notExists(Paths.get(compilePath))){
                Map<String,Object> map = new HashMap<>();
                map.put("name",root.elementText("artifactId"));
                map.put("package",packageName);
                FileOutputStream outputStream = new FileOutputStream(compilePath);
                outputStream.write(new GsonBuilder().setPrettyPrinting().create().toJson(map).getBytes());
                outputStream.flush();
                outputStream.close();
            }

            if (!"ipojo-bundle".equals(root.elementText("packaging"))){
                Element packaging = root.element("packaging");
                if (packaging == null){
                    packaging = root.addElement("packaging");
                }
                packaging.setText("ipojo-bundle");
            }

            Element pluginRepositories = root.element("pluginRepositories");
            if (pluginRepositories == null){
                pluginRepositories = root.addElement("pluginRepositories");
            }
            boolean pluginRepositoryExist = pluginRepositories.elements("pluginRepository").stream()
                    .anyMatch(e -> "http://nexus.guyi-maple.top/repository/maven-public/".equals(((Element)e).elementText("url")));
            if (!pluginRepositoryExist){
                Element pluginRepository = pluginRepositories.addElement("pluginRepository");
                pluginRepository.addElement("id").setText("guyi");
                pluginRepository.addElement("name").setText("guyi");
                pluginRepository.addElement("url").setText("http://nexus.guyi-maple.top/repository/maven-public/");
            }


            Element repositories = root.element("repositories");
            if (repositories == null){
                repositories = root.addElement("repositories");
            }
            boolean repositoryExist = repositories.elements("repository").stream()
                    .anyMatch(e -> "http://nexus.guyi-maple.top/repository/maven-public/".equals(((Element)e).elementText("url")));
            if (!repositoryExist){
                Element repository = repositories.addElement("repository");
                repository.addElement("id").setText("guyi");
                repository.addElement("url").setText("http://nexus.guyi-maple.top/repository/maven-public/");
            }

            Element build = root.element("build");
            if (build == null){
                build = root.addElement("build");
            }
            Element plugins = build.element("plugins");
            if (plugins == null){
                plugins = build.addElement("plugins");
            }
            boolean pluginExist = plugins.elements("plugin").stream()
                    .anyMatch(e -> "top.guyi.iot.ipojo.compile".equals(((Element)e).elementText("groupId")));
            if (!pluginExist){
                Element plugin = plugins.addElement("plugin");
                plugin.addElement("groupId").setText("top.guyi.iot.ipojo.compile");
                plugin.addElement("artifactId").setText("compile-maven-plugin");
                plugin.addElement("version").setText("1.0.0.2-SNAPSHOT");
                plugin.addElement("extensions").setText("true");
            }

            Element dependencies = root.element("dependencies");
            if (dependencies == null){
                dependencies = root.addElement("dependencies");
            }
            boolean dependencyExist = dependencies.elements("dependency").stream()
                    .anyMatch(e -> "top.guyi.iot.ipojo".equals(((Element)e).elementText("groupId")));
            if (!dependencyExist){
                Element dependency = dependencies.addElement("dependency");
                dependency.addElement("groupId").setText("top.guyi.iot.ipojo");
                dependency.addElement("artifactId").setText("ipojo");
                dependency.addElement("version").setText("1.0.0.2-SNAPSHOT");
            }

            Writer osWrite = new OutputStreamWriter(new FileOutputStream(pomPath));
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            XMLWriter writer = new XMLWriter(osWrite, format);
            writer.write(document);
            writer.flush();
            return true;
        } catch (DocumentException | IOException ex) {
            ex.printStackTrace();
            Messages.showMessageDialog(project,ex.getMessage(),"初始化失败", Messages.getErrorIcon());
            return false;
        }
    }
}
