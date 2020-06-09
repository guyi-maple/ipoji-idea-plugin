package top.guyi.ipojo.idea.plugin.felix;

import com.intellij.codeInsight.completion.*;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import top.guyi.ipojo.idea.plugin.Icons;
import top.guyi.ipojo.idea.plugin.element.entry.ElementDecorator;
import top.guyi.ipojo.idea.plugin.element.repository.AbstractElementRepository;
import top.guyi.ipojo.idea.plugin.element.repository.defaults.FelixElementRepository;
import top.guyi.ipojo.idea.plugin.element.utils.ElementUtils;

import javax.swing.*;

public class FelixCompletionContributor extends CompletionContributor {

    private final Icon icon = Icons.FELIX_ICON;
    private final AbstractElementRepository repository = new FelixElementRepository();

    public FelixCompletionContributor(){
        extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement(PsiElement.class),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet resultSet) {
                        if (ElementUtils.isFelix(parameters.getPosition())){
                            ElementUtils.getDecorator(icon,repository,parameters,resultSet)
                                    .ifPresent(ElementDecorator::auto);
                        }
                    }
                }
        );
    }

}
