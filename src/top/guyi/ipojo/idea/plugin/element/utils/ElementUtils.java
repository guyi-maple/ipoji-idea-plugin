package top.guyi.ipojo.idea.plugin.element.utils;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.psi.PsiElement;
import top.guyi.ipojo.idea.plugin.element.repository.AbstractElementRepository;
import top.guyi.ipojo.idea.plugin.element.entry.ElementDecorator;

import javax.swing.*;
import java.util.Optional;

/**
 * @author guyi
 * 节点工具
 */
public class ElementUtils {

    public static boolean isFelix(PsiElement element){
        return element != null && element.getContainingFile().getName().equals("configuration.felix");
    }

    public static boolean isConfiguration(PsiElement element){
        return element != null && element.getContainingFile().getName().endsWith(".configuration");
    }

    public static boolean isCompile(PsiElement element){
        return element != null && element.getContainingFile().getName().equals("ipojo.compile");
    }

    public static boolean isAttach(PsiElement element){
        return element != null && element.getContainingFile().getName().endsWith(".attach");
    }

    public static Optional<ElementDecorator> getDecorator(
            Icon defaultIcon,
            AbstractElementRepository repository,
            CompletionParameters parameters,
            CompletionResultSet resultSet){

        if (parameters.getPosition().getContext() instanceof JsonStringLiteral){
            return Optional.of(new ElementDecorator(
                    defaultIcon,
                    repository,
                    parameters,
                    resultSet)
            );
        }

        return Optional.empty();
    }
}
