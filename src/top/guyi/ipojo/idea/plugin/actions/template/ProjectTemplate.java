package top.guyi.ipojo.idea.plugin.actions.template;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import top.guyi.ipojo.idea.plugin.actions.template.change.ChangeItem;
import top.guyi.ipojo.idea.plugin.actions.template.parser.FileElement;
import top.guyi.ipojo.idea.plugin.actions.template.parser.FileParser;
import top.guyi.ipojo.idea.plugin.actions.template.parser.FileParserFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProjectTemplate {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static ProjectTemplate getInstance(String url) throws IOException {
        return getInstance(new URL(url).openStream());
    }

    public static ProjectTemplate getInstance(InputStream inputStream) throws IOException {
        return new Gson().fromJson(
                IOUtils.toString(inputStream, StandardCharsets.UTF_8),
                ProjectTemplate.class
        );
    }

    private List<TemplateParameter> parameters = Collections.emptyList();
    private List<TemplateAction<Object>> out = Collections.emptyList();
    private List<TemplateAction<List<ChangeItem>>> change = Collections.emptyList();

    public List<TemplateParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<TemplateParameter> parameters) {
        this.parameters = parameters;
    }

    public List<TemplateAction<Object>> getOut() {
        return out;
    }

    public void setOut(List<TemplateAction<Object>> out) {
        this.out = out;
    }

    public List<TemplateAction<List<ChangeItem>>> getChange() {
        return change;
    }

    public void setChange(List<TemplateAction<List<ChangeItem>>> change) {
        this.change = change;
    }

    private String getPath(String basedir,String path, Map<String,String> parameters){
        return basedir + "/" + this.replaceParameter(path,parameters);
    }

    public void execute(String basedir,Map<String,String> parameters) throws Exception {
        for (TemplateAction<List<ChangeItem>> action : this.change) {
            String path = this.getPath(basedir,action.getFile(),parameters);
            if (Files.notExists(Paths.get(path))){
                throw new RuntimeException(action.getError() == null ? String.format("文件%s不存在",parameters) : action.getError());
            }
            this.handleChange(path,action,parameters);
        }

        for (TemplateAction<Object> action : this.out) {
            String path = this.getPath(basedir,action.getFile(),parameters);
            this.outWrite(path,action.getContent(),parameters);
        }
    }

    private static final Pattern pattern = Pattern.compile("\\{([^\\{^\\}]+)\\}");

    private String replaceParameter(String origin,Map<String,String> parameters){
        Matcher matcher = pattern.matcher(origin);
        while (matcher.find()){
            String name = matcher.group(1);
            String value = parameters.get(name);
            if (value != null){
                origin = origin.replace(String.format("{%s}",name),value);
            }
        }
        return origin;
    }

    private void handleChange(String path,TemplateAction<List<ChangeItem>> action,Map<String,String> parameters) throws Exception {
        FileParser parser = FileParserFactory.getParser(path);
        for (ChangeItem item : action.getContent()) {
            List<FileElement> elements = parser.get(this.replaceParameter(item.getKey(),parameters));

            Map<String,String> map = new HashMap<>();
            item.getValue().forEach((key,value) -> map.put(
                    this.replaceParameter(key,parameters),
                    this.replaceParameter(value,parameters)
            ));
            item.setValue(map);

            if (item.getCondition() != null){
                elements.stream()
                        .filter(e -> e.findChildren(this.replaceParameter(item.getCondition(),parameters))
                                .stream()
                                .findFirst()
                                .map(child -> child.isCreate()
                                        || child.getValue().equals(this.replaceParameter(item.getConditionValue(),parameters)))
                                .orElse(false)
                        )
                        .findFirst()
                        .orElseGet(() -> parser.createElement(item.getKey()))
                        .setValue(item.getValue());
            }else{
                elements.forEach(e -> e.setValue(item.getValue()));
            }
        }
        parser.write(path);
    }

    private void outWrite(String path,Object object,Map<String,String> parameters) throws IOException {
        File parent = new File(path).getParentFile();
        if (!parent.exists()){
            parent.mkdirs();
        }

        String json = gson.toJson(object);
        json = this.replaceParameter(json,parameters);
        FileOutputStream outputStream = new FileOutputStream(path);
        outputStream.write(json.getBytes());
        outputStream.flush();
        outputStream.close();
    }

}
