package top.guyi.ipojo.idea.plugin.element.handler;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.vfs.VirtualFile;
import top.guyi.ipojo.idea.plugin.element.entry.ElementDecorator;

import javax.swing.*;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public interface ElementHandler {

    static Project getProject(ElementDecorator decorator){
        return decorator.getParameters().getEditor().getProject();
    }

    static LibraryTable getLibraryTable(ElementDecorator decorator){
        return LibraryTablesRegistrar
                .getInstance()
                .getLibraryTable(ElementHandler.getProject(decorator));
    }

    static Module getModule(ElementDecorator decorator){
        return ModuleUtil.findModuleForFile(
                decorator.getParameters().getOriginalFile().getVirtualFile(),
                ElementHandler.getProject(decorator)
        );
    }

    static JarFile getJarFile(String value) {
        try{
            value = "jar:file:/" + value.substring(7);
            URL url = new URL(value);
            return ((JarURLConnection) url.openConnection()).getJarFile();
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    static Set<String> getSourceRoots(Module module){
        return Arrays.stream(ModuleRootManager.getInstance(module).getSourceRoots())
                .map(VirtualFile::getPath)
                .collect(Collectors.toSet());
    }

    String name();

    List<LookupElement> handle(ElementDecorator decorator, ElementHandlerEntry entry, Icon icon);
}
