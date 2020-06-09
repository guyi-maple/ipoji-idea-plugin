package top.guyi.ipojo.idea.plugin.actions.template.parser.xml;

import org.dom4j.Element;
import top.guyi.ipojo.idea.plugin.actions.template.parser.FileElement;

import java.util.LinkedList;
import java.util.List;

public class XmlFileElement implements FileElement {

    private boolean create;
    private final Element element;

    public XmlFileElement(Element element) {
        this.element = element;
    }

    public XmlFileElement(Element element, boolean create) {
        this.element = element;
        this.create = create;
    }

    @Override
    public boolean isCreate() {
        return this.create;
    }

    @Override
    public String getName() {
        return this.element.getName();
    }

    @Override
    public String getValue() {
        return this.element.getText();
    }

    @Override
    public void setValue(String value) {
        this.element.setText(value);
    }

    @Override
    public List<FileElement> findChildren(String elementName) {
        List<FileElement> elements = new LinkedList<>();
        for (Object element : this.element.elements(elementName)) {
            elements.add(new XmlFileElement((Element) element));
        }
        if (elements.isEmpty()){
            Element element = this.element.addElement(elementName);
            elements.add(new XmlFileElement(element,true));
        }
        return elements;
    }

    @Override
    public void addElement(String key, String value) {
        Element element = this.element.element(key);
        if (element == null || !"".equals(element.getText())){
            element = this.element.addElement(key);
        }
        element.setText(value);
    }
}
