package top.guyi.ipojo.idea.plugin.element.handler;

import java.util.Map;
import java.util.Optional;

public class ElementHandlerEntry {

    private String name;
    private Map<String,String> parameters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getParameter(String name){
        return Optional.ofNullable(this.parameters)
                .flatMap(parameters -> parameters.entrySet()
                        .stream()
                        .filter(e -> e.getKey().equals(name))
                        .findFirst()
                        .map(Map.Entry::getValue))
                .orElse(null);
    }
}
