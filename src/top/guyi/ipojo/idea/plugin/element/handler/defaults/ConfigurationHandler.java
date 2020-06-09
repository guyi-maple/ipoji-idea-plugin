package top.guyi.ipojo.idea.plugin.element.handler.defaults;

import com.google.gson.Gson;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.LibraryTable;
import javassist.*;
import top.guyi.ipojo.idea.plugin.element.entry.ElementDecorator;
import top.guyi.ipojo.idea.plugin.element.handler.ElementHandler;
import top.guyi.ipojo.idea.plugin.element.handler.ElementHandlerEntry;
import top.guyi.ipojo.idea.plugin.element.handler.entry.ComponentInfo;
import top.guyi.ipojo.idea.plugin.element.handler.entry.ConfigurationKeyEntry;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

class NameEntry {
    public NameEntry(String name, String file) {
        this.name = name;
        this.file = file;
    }
    String name;
    String file;
}

public class ConfigurationHandler implements ElementHandler {

    private final Gson gson = new Gson();
    private final Pattern pattern = Pattern.compile("@ConfigurationKey\\s*\\([^\\)]*\\)");
    private final Pattern patternKey = Pattern.compile("@ConfigurationKey\\s*\\([^\\)]*(key\\s*=\\s*\"([^\"]+)\\s*\")[^\\)]*\\)");
    private final Pattern patternFile = Pattern.compile("@ConfigurationKey\\s*\\([^\\)]*(file\\s*=\\s*([^,^\\s]+)\\s*)[^\\)]*\\)");
    private final Pattern patternRemark = Pattern.compile("@ConfigurationKey\\s*\\([^\\)]*(remark\\s*=\\s*\"([^\"]+)\\s*\")[^\\)]*\\)");
    private final Map<String,Set<NameEntry>> nameCache = new HashMap<>();

    @Override
    public String name() {
        return "configuration";
    }

    private String formatJarUrl(String url){
        return url.substring(6,url.length() - 2);
    }

    private List<File> scanner(File root){
        List<File> tmp = Optional.ofNullable(root.listFiles())
                .map(Arrays::asList)
                .orElseGet(Collections::emptyList);

        List<File> files = tmp.stream()
                .filter(file -> file.getName().endsWith(".java"))
                .collect(Collectors.toList());


        files.addAll(
                tmp.stream()
                        .filter(File::isDirectory)
                        .map(this::scanner)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList())
        );
        return files;
    }

    private Set<NameEntry> getKeysByJar(JarFile jar,String url, boolean isFile){

        if (nameCache.containsKey(url)){
            return nameCache.get(url);
        }

        try {
            ClassPool pool = new ClassPool();
            pool.appendClassPath(url);
            ZipEntry entry = jar.getEntry("component.info");
            if (entry != null){
                ComponentInfo info = this.gson.fromJson(
                        new InputStreamReader(jar.getInputStream(entry)),
                        ComponentInfo.class
                );
                if (info.getConfigurations() != null && !info.getConfigurations().isEmpty()){
                    Set<NameEntry> names = new HashSet<>();
                    for (ConfigurationKeyEntry configuration : info.getConfigurations()) {
                        if (configuration.isFile() && isFile){
                            String remark = configuration.getRemark();
                            String simpleName = configuration.getClassName() == null ? "" : configuration.getClassName();
                            names.add(new NameEntry(configuration.getKey(),"".equals(remark) ? simpleName : remark + "," + simpleName));
                        }
                    }
                    return names;
                }
            }
        } catch (IOException | NotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Set<NameEntry> getByLibrary(ElementDecorator decorator, boolean isFile){
        LibraryTable table = ElementHandler.getLibraryTable(decorator);

        return Arrays.stream(table.getLibraries())
                .map(library -> library.getUrls(OrderRootType.CLASSES))
                .flatMap(Arrays::stream)
                .map(url -> this.getKeysByJar(
                        ElementHandler.getJarFile(url),
                        this.formatJarUrl(url),
                        isFile
                )).filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private Set<NameEntry> getKeysByFile(File file, boolean isFile){
        try {
            StringBuilder sb = new StringBuilder();
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()){
                sb.append(scanner.nextLine());
            }
            scanner.close();

            String fileName = file.getName().replace(".java","");
            Set<NameEntry> names = new HashSet<>();
            Matcher matcher = pattern.matcher(sb.toString());
            while (matcher.find()){
                String text = matcher.group();

                String key = null;
                Matcher tmp = patternKey.matcher(text);
                if (tmp.find()){
                    key = tmp.group(2);
                }

                String remark = null;
                tmp = patternRemark.matcher(text);
                if (tmp.find()){
                    remark = tmp.group(2);
                }

                boolean f = false;
                tmp = patternFile.matcher(text);
                if (tmp.find()){
                    f = Optional.ofNullable(tmp.group(2))
                            .map(Boolean::parseBoolean)
                            .orElse(false);
                }

                if (key != null && (!isFile || f)){
                    names.add(new NameEntry(key,remark == null ? fileName: remark + "," + fileName));
                }
            }
            return names;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Set<NameEntry> getByProject(ElementDecorator decorator,boolean isFile){
        return ElementHandler.getSourceRoots(ElementHandler.getModule(decorator))
                .stream()
                .map(File::new)
                .map(this::scanner)
                .flatMap(Collection::stream)
                .map(file -> this.getKeysByFile(file,isFile))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public List<LookupElement> handle(ElementDecorator decorator, ElementHandlerEntry entry, Icon icon) {
        List<String> scope = Optional.ofNullable(entry.getParameter("scope"))
                .map(s -> Arrays.asList(s.split(",")))
                .orElseGet(() -> Collections.singletonList("project,classpath"));

        boolean isFile = Optional.ofNullable(entry.getParameter("file")).isPresent();

        Set<NameEntry> names = new HashSet<>();

        if (scope.contains("project")){
            names.addAll(this.getByProject(decorator,isFile));
        }

        if (scope.contains("classpath")){
            names.addAll(this.getByLibrary(decorator,isFile));
        }

        return names.stream()
                .map(name -> (LookupElement) LookupElementBuilder.create(name.name).withTypeText(name.file).withIcon(icon))
                .collect(Collectors.toList());
    }

}
