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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PackageElementHandler implements ElementHandler {

    @Override
    public String name() {
        return "package";
    }

    private final Pattern pattern = Pattern.compile("/");

    private int getCount(String value){
        int count = 0;
        Matcher m = pattern.matcher(value);
        while (m.find()) {
            count++;
        }
        return count;
    }

    private Set<String> getByLibrary(LibraryTable table,String prefix){
        Set<String> packages = new HashSet<>();
        int count = getCount(prefix) + 1;
        for (Library library : table.getLibraries()) {
            for (String value : library.getUrls(OrderRootType.CLASSES)) {
                JarFile jar = ElementHandler.getJarFile(value);
                if (jar != null){
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()){
                        JarEntry entry = entries.nextElement();
                        if (entry.isDirectory()
                                && entry.getName().startsWith(prefix)
                                && this.getCount(entry.getName()) == count){
                            packages.add(entry.getName().substring(0,entry.getName().length() - 1).replaceAll("/","."));
                        }
                    }
                }
            }
        }
        return packages;
    }

    private Set<String> getByProject(Module module, String prefix){
        String base;
        String pre;
        if (prefix.contains("/")){
            if (prefix.endsWith("/")){
                base = prefix;
                pre = "";
            }else{
                int index = prefix.lastIndexOf("/");
                base = prefix.substring(0,index);
                pre = prefix.substring(index + 1);
            }
        }else{
            base = "";
            pre = prefix;
        }

        Set<String> packages = new HashSet<>();
        if (module != null){
            VirtualFile[] roots = ModuleRootManager.getInstance(module).getSourceRoots();
            for (VirtualFile root : roots) {
                String path = String.format("%s/%s",root.getPath(),base);
                File[] fs = new File(path).listFiles(f -> f.isDirectory() && f.getName().startsWith(pre));
                if (fs != null){
                    for (File f : fs) {
                        String name = f.getAbsolutePath().replaceAll("[\\/]+","/");
                        name = name.replace(root.getPath(),"");
                        name = name.startsWith("/") ? name.substring(1) : name;
                        packages.add(name.replaceAll("\\/","."));
                    }
                }
            }
        }
        return packages;
    }

    @Override
    public List<LookupElement> handle(ElementDecorator decorator, ElementHandlerEntry elementHandlerEntry, Icon icon) {
        Project project = decorator.getParameters().getEditor().getProject();
        if (project != null){
            String prefix = decorator.getValue().replaceAll("\\.","/");

            Set<String> packages = new HashSet<>(this.getByProject(
                    ElementHandler.getModule(decorator),
                    prefix
            ));

            if (!"project".equals(elementHandlerEntry.getParameter("scope"))){
                packages.addAll(this.getByLibrary(ElementHandler.getLibraryTable(decorator),prefix));
            }

            List<LookupElement> result = new LinkedList<>();
            packages.forEach(packageName -> result.add(
                    LookupElementBuilder
                            .create(packageName)
                            .withIcon(icon)
            ));

            return result;
        }
        return null;
    }

}
