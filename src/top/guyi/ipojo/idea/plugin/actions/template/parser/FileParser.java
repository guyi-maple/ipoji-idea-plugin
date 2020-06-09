package top.guyi.ipojo.idea.plugin.actions.template.parser;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public interface FileParser {

    default List<String> getKeys(String key){
        return Arrays.asList(key.split("\\."));
    }

    default List<FileElement> get(String key){
        List<String> keys = this.getKeys(key);
        List<FileElement> elements = this.getTop(keys.get(0));
        for (int i = 1; i < keys.size(); i++) {
            elements = this.get(elements,keys.get(i));
        }
        return elements;
    }

    default Optional<FileElement> getFirst(String key){
        return this.get(key).stream().findFirst();
    }

    default Optional<FileElement> getLast(String key){
        return Optional.ofNullable(this.get(key))
                .filter(es -> es.size() > 0)
                .map(es -> es.get(es.size() - 1));
    }

    default List<FileElement> get(List<FileElement> elements,String elementName){
        return elements
                .stream()
                .map(e -> e.findChildren(elementName))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    List<FileElement> getTop(String elementName);

    void write(String path) throws IOException;

    FileElement createElement(String key);

}
