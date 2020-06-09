package top.guyi.ipojo.idea.plugin.configuration;

import com.intellij.codeInsight.completion.*;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import top.guyi.ipojo.idea.plugin.Icons;
import top.guyi.ipojo.idea.plugin.element.entry.ElementDecorator;
import top.guyi.ipojo.idea.plugin.element.handler.ElementHandler;
import top.guyi.ipojo.idea.plugin.element.handler.ElementHandlerEntry;
import top.guyi.ipojo.idea.plugin.element.handler.ElementHandlerFactory;
import top.guyi.ipojo.idea.plugin.element.repository.AbstractElementRepository;
import top.guyi.ipojo.idea.plugin.element.repository.defaults.CompileElementRepository;
import top.guyi.ipojo.idea.plugin.element.utils.ElementUtils;

import javax.swing.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationCompletionContributor extends CompletionContributor {

    private final Icon icon = Icons.CONFIGURATION_ICON;
    private final ElementHandler handler = ElementHandlerFactory.CONFIGURATION;
    private final ElementHandlerEntry entry;

    public ConfigurationCompletionContributor(){
        entry = new ElementHandlerEntry();
        Map<String,String> parameters = new HashMap<>();
        parameters.put("scope","project,classpath");
        parameters.put("file","true");
        entry.setParameters(parameters);

        extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement(PsiElement.class),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet resultSet) {
                        if (ElementUtils.isConfiguration(parameters.getPosition())){
                            ElementUtils.getDecorator(icon,null,parameters,resultSet)
                                    .ifPresent(decorator -> handler.handle(decorator,entry,icon)
                                            .forEach(resultSet::addElement));
                        }
                    }
                }
        );
    }

}
