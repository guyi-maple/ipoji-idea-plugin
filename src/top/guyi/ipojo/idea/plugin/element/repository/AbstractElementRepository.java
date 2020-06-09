package top.guyi.ipojo.idea.plugin.element.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import top.guyi.ipojo.idea.plugin.element.Element;

import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AbstractElementRepository {

    private static final Gson gson = new Gson();

    private final List<Element> elements;

    public AbstractElementRepository(String path){
        elements = gson.fromJson(
                new InputStreamReader(Objects.requireNonNull(AbstractElementRepository.class.getClassLoader().getResourceAsStream(path))),
                new TypeToken<List<Element>>(){}.getType()
        );
    }

    public List<Element> get(String prefix){
        return get(prefix,elements);
    }

    public List<Element> get(String prefix, List<Element> elements){
        return elements
                .stream()
                .filter(e -> e.getValue().contains(prefix))
                .collect(Collectors.toList());
    }

    public List<Element> get(List<String> parent,String prefix){
        return get(parent,prefix,elements);
    }

    public List<Element> get(List<String> parent,String prefix,List<Element> elements){
        for (String name : parent) {
            elements = get(name,elements)
                    .stream()
                    .map(Element::getChildren)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }
        return get(prefix,elements);
    }


}
