package top.guyi.ipojo.idea.plugin.element.handler.defaults;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.vfs.VirtualFile;
import top.guyi.ipojo.idea.plugin.element.entry.ElementDecorator;
import top.guyi.ipojo.idea.plugin.element.handler.ElementHandler;
import top.guyi.ipojo.idea.plugin.element.handler.ElementHandlerEntry;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class FileElementHandler implements ElementHandler {

    @Override
    public String name() {
        return "file";
    }

    private Set<String> getFileNames(String path,String prefix,String suffix){
        return Optional.ofNullable(new File(path).listFiles(f -> f.getName().endsWith(suffix)))
                .map(Arrays::stream)
                .map(fs -> fs.map(File::getName)
                        .filter(name -> name.contains(prefix))
                        .collect(Collectors.toSet()))
                .orElseGet(Collections::emptySet);
    }

    private Set<String> getByBaseDir(Project project,String prefix,String suffix){
        return this.getFileNames(project.getBasePath(),prefix,suffix);
    }

    private Set<String> getByProject(Module module, String prefix, String suffix){
        Set<String> names = new HashSet<>();
        if (module != null){
            for (String root : ElementHandler.getSourceRoots(module)) {
                names.addAll(this.getFileNames(root,prefix,suffix));
            }
        }
        return names;
    }

    private Set<String> getByLibrary(LibraryTable table, String prefix, String suffix){
        Set<String> packages = new HashSet<>();
        for (Library library : table.getLibraries()) {
            for (String value : library.getUrls(OrderRootType.CLASSES)) {
                try {
                    value = "jar:file:/" + value.substring(7);
                    URL url = new URL(value);
                    JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()){
                        JarEntry entry = entries.nextElement();
                        if (entry.getName().endsWith(suffix)){
                            packages.add(entry.getName().replace("/",""));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return packages;
    }

    @Override
    public List<LookupElement> handle(ElementDecorator decorator, ElementHandlerEntry entry, Icon icon) {
        return Optional.ofNullable(decorator.getParameters().getEditor().getProject())
                .map(project -> {
                    String prefix = decorator.getValue();
                    String suffix = entry.getParameter("suffix");
                    Set<String> scope = new HashSet<>(Arrays.asList(
                            Optional.ofNullable(entry.getParameter("scope"))
                                    .map(String::toLowerCase)
                                    .orElse("basedir,project,classpath")
                                    .split(",")
                    ));

                    Set<String> names = new HashSet<>();

                    if (scope.contains("basedir")){
                        names.addAll(this.getByBaseDir(project,prefix,suffix));
                    }

                    if (scope.contains("classpath")){
                        names.addAll(this.getByLibrary(
                                ElementHandler.getLibraryTable(decorator),
                                prefix,
                                suffix
                        ));
                    }

                    if (scope.contains("project")){
                        names.addAll(this.getByProject(
                                ElementHandler.getModule(decorator),
                                prefix,
                                suffix
                        ));
                    }
                    return names.stream()
                            .map(name -> (LookupElement) LookupElementBuilder
                                    .create(name.replace(suffix,""))
                                    .withTypeText(name)
                                    .withIcon(icon)
                            )
                            .collect(Collectors.toList());
                })
                .orElseGet(Collections::emptyList);
    }

}
