package top.guyi.ipojo.idea.plugin.attach;

import com.intellij.codeInsight.completion.*;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import top.guyi.ipojo.idea.plugin.Icons;
import top.guyi.ipojo.idea.plugin.element.entry.ElementDecorator;
import top.guyi.ipojo.idea.plugin.element.repository.AbstractElementRepository;
import top.guyi.ipojo.idea.plugin.element.repository.defaults.AttachElementRepository;
import top.guyi.ipojo.idea.plugin.element.utils.ElementUtils;

import javax.swing.*;

public class AttachCompletionContributor extends CompletionContributor {

    private final Icon icon = Icons.ATTACH_ICON;
    private final AbstractElementRepository repository = new AttachElementRepository();

    public AttachCompletionContributor(){
        extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement(PsiElement.class),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet resultSet) {
                        if (ElementUtils.isAttach(parameters.getPosition())){
                            ElementUtils.getDecorator(icon,repository,parameters,resultSet)
                                    .ifPresent(ElementDecorator::auto);
                        }
                    }
                }
        );
    }

}
