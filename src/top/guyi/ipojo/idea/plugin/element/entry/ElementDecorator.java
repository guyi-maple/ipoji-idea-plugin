package top.guyi.ipojo.idea.plugin.element.entry;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.json.psi.JsonArray;
import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonProperty;
import com.intellij.psi.PsiElement;
import top.guyi.ipojo.idea.plugin.Icons;
import top.guyi.ipojo.idea.plugin.element.repository.AbstractElementRepository;
import top.guyi.ipojo.idea.plugin.element.handler.ElementHandlerFactory;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

public class ElementDecorator {

    private PsiElement _this;
    private final Icon defaultIcon;
    private final CompletionParameters parameters;
    private final CompletionResultSet resultSet;
    private final AbstractElementRepository repository;

    public ElementDecorator(Icon defaultIcon,AbstractElementRepository repository,CompletionParameters parameters, CompletionResultSet resultSet) {
        this.defaultIcon = defaultIcon;
        this.repository = repository;
        this.parameters = parameters;
        this.resultSet = resultSet;
    }

    public CompletionParameters getParameters() {
        return parameters;
    }

    private String getValue(String value){
        return value
                .replace("IntellijIdeaRulezzz","")
                .replace("\"","")
                .trim();
    }

    public String getValue(){
        return this.getValue(this.getThis().getText());
    }

    public PsiElement getThis(){
        if (this._this == null){
            this._this = this.parameters.getPosition().getContext();
        }
        return this._this;
    }

    public String getParentPropertyName(){
        return this.getParentPropertyName(this.getThis());
    }

    public String getParentPropertyName(PsiElement element){
        element = element.getParent().getParent();
        if (element instanceof JsonProperty){
            return this.getValue(((JsonProperty) element).getName());
        }
        return null;
    }

    public String getPropertyName(){
        return this.getPropertyName(this.getThis());
    }

    public String getPropertyName(JsonProperty property){
        return this.getValue(property.getChildren()[0].getText());
    }

    public String getPropertyName(PsiElement element){
        if (this.isPropertyName(element)){
            return this.getValue(element.getText());
        }else{
            return this.getValue(element.getParent().getChildren()[0].getText());
        }
    }

    public int getIndex(){
        return this.getIndex(this.getThis());
    }

    public int getIndex(PsiElement element){
        PsiElement[] children = element.getParent().getChildren();
        for (int i = 0; i < children.length; i++) {
            if (children[i] == this.getThis()){
                return i;
            }
        }
        return -1;
    }

    public boolean isPropertyName(){
        return this.isPropertyName(this.getThis());
    }

    public boolean isPropertyName(PsiElement element){
        return this.getIndex(element) == 0 && !(element.getParent() instanceof JsonArray);
    }

    public boolean isPropertyValue(){
        return this.isPropertyValue(this.getThis());
    }

    public boolean isPropertyValue(PsiElement element){
        return this.getIndex(element) == 1 || element.getParent() instanceof JsonArray;
    }

    private List<String> getPropertyNames(PsiElement element,List<String> names){
        if (element.getParent() instanceof JsonFile){
            List<String> tmp = new LinkedList<>();
            for (int i = (names.size() - 1); i >= 0; i--) {
                tmp.add(names.get(i));
            }
            return tmp;
        }
        if (element instanceof JsonProperty){
            names.add(this.getPropertyName((JsonProperty) element));
        }

        return this.getPropertyNames(element.getParent(),names);
    }

    public void completePropertyName(List<String> parent){
        if (this.isPropertyName()){
            List<LookupElement> elements = new LinkedList<>();
            if (parent.size() > 0){
                List<String> tmp = parent.subList(0,parent.size() - 1);
                repository.get(tmp,parent.get(parent.size() - 1))
                        .stream()
                        .filter(e -> e.getHandler() != null)
                        .findFirst()
                        .ifPresent(e -> elements.addAll(
                                ElementHandlerFactory.get(e.getHandler().getName())
                                        .map(handler ->
                                                handler.handle(this, e.getHandler(), Icons.get(e.getIcon(), defaultIcon)))
                                        .orElse(Collections.emptyList())
                                )
                        );
            }

            repository.get(parent,this.getValue())
                    .forEach(e -> elements.add(LookupElementBuilder
                            .create(e.getValue())
                            .withIcon(defaultIcon)
                            .withTypeText(e.getText()))
                    );

            elements.forEach(this.resultSet::addElement);
        }
    }

    public void completePropertyValue(List<String> parent){
        if (this.isPropertyValue()){
            String propertyName = this.getPropertyName();

            if (parent.size() > 0){
                if (this.getThis().getParent() instanceof JsonArray){
                    parent.remove(parent.size() - 1);
                    propertyName = this.getPropertyName(this.getThis().getParent());
                }
            }

            if (parent.size() > 0){
                if (parent.get(parent.size() - 1).equals(propertyName)){
                    parent.remove(parent.size() - 1 );
                }
            }

            repository.get(parent,propertyName)
                    .stream()
                    .findFirst()
                    .ifPresent(element -> {
                        List<LookupElement> elements = Collections.emptyList();
                        if (element.getHandler() != null){
                            elements = ElementHandlerFactory.get(element.getHandler().getName())
                                    .map(handler -> handler.handle(this,element.getHandler(),Icons.get(element.getIcon(),defaultIcon)))
                                    .orElse(Collections.emptyList());
                        }else if (element.getDefaults() != null){
                            elements = element.getDefaults()
                                    .stream()
                                    .map(e -> LookupElementBuilder
                                            .create(e.getValue())
                                            .withTypeText(e.getText())
                                            .withIcon(Icons.get(element.getIcon(),defaultIcon)))
                                    .collect(Collectors.toList());
                        }
                        elements.forEach(this.resultSet::addElement);
                    });
        }
    }

    public void auto(){
        List<String> parent = this.getPropertyNames(this.getThis().getParent(),new LinkedList<>());

        int last = parent.size() - 1;
        if (parent.get(last).equals(this.getValue())){
            parent.remove(last);
        }

        this.completePropertyName(parent);
        this.completePropertyValue(parent);
    }



}
