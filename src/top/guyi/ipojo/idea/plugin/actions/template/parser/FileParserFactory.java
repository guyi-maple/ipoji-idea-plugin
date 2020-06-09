package top.guyi.ipojo.idea.plugin.actions.template.parser;

import top.guyi.ipojo.idea.plugin.actions.template.ProjectTemplate;
import top.guyi.ipojo.idea.plugin.actions.template.parser.xml.XmlFileParser;

import java.io.File;
import java.util.List;

public class FileParserFactory {

    public static FileParser getParser(String path) throws Exception {
        if (path.endsWith(".xml")){
            return new XmlFileParser(path);
        }
        throw new RuntimeException("找不到对应的模板文件解析器");
    }

    public static void main(String[] args) throws Exception {
        String path = new File("test.xml").getAbsolutePath();
        FileParser parser = getParser(path);
        ProjectTemplate template = ProjectTemplate.getInstance("file:////Users/guyi/Documents/Work/IDEA/ipojo-compile-plugin/test.template.json");
        template.getChange().forEach(change -> change.getContent().forEach(item -> {
           List<FileElement> elements = parser.get(item.getKey());
           elements.forEach(e -> e.setValue(item.getValue()));
        }));
        parser.write(path);
    }

}
