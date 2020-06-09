package top.guyi.ipojo.idea.plugin.actions.template.parser.xml;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import top.guyi.ipojo.idea.plugin.actions.template.parser.FileElement;
import top.guyi.ipojo.idea.plugin.actions.template.parser.FileParser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

public class XmlFileParser implements FileParser {

    private final Document document;
    private final Element root;

    public XmlFileParser(String path) throws DocumentException {
        SAXReader reader = new SAXReader();
        this.document = reader.read(path);
        this.root = document.getRootElement();
    }

    @Override
    public List<FileElement> getTop(String elementName) {
        List<FileElement> elements = new LinkedList<>();
        for (Object element : this.root.elements(elementName)) {
            elements.add(new XmlFileElement((Element) element));
        }
        if (elements.isEmpty()){
            Element element = this.root.addElement(elementName);
            elements.add(new XmlFileElement(element, true));
        }
        return elements;
    }

    @Override
    public void write(String path) throws IOException {
        Writer osWrite = new OutputStreamWriter(new FileOutputStream(path));
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        XMLWriter writer = new XMLWriter(osWrite, format);
        writer.write(document);
        writer.flush();
    }

    @Override
    public FileElement createElement(String key) {
        List<String> keys = this.getKeys(key);
        if (keys.size() == 1){
            return new XmlFileElement(root.addElement(key));
        }

        Element element = this.root;
        for (int i = 0; i < (keys.size() - 1); i++) {
            Element tmp = element.element(keys.get(i));
            if (tmp == null){
                element = element.addElement(keys.get(i));
            }else{
                element = tmp;
            }
        }

        String lastName = keys.get(keys.size() - 1);
        Element last = element.element(lastName);
        if (last == null){
            last = element.addElement(lastName);
        }else{
            last = "".equals(last.getText()) ? last : element.addElement(lastName);
        }

        return new XmlFileElement(last);
    }

}
