package top.guyi.ipojo.idea.plugin.element.handler;

import top.guyi.ipojo.idea.plugin.element.handler.defaults.ConfigurationHandler;
import top.guyi.ipojo.idea.plugin.element.handler.defaults.FileElementHandler;
import top.guyi.ipojo.idea.plugin.element.handler.defaults.PackageElementHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ElementHandlerFactory {

    private static final Map<String,ElementHandler> handlers;

    public static final ElementHandler CONFIGURATION;

    static {
        handlers = new HashMap<>();

        ElementHandler handler = new PackageElementHandler();
        handlers.put(handler.name(),handler);

        handler = new FileElementHandler();
        handlers.put(handler.name(),handler);

        CONFIGURATION = new ConfigurationHandler();
        handlers.put(CONFIGURATION.name(),CONFIGURATION);
    }

    public static Optional<ElementHandler> get(String name){
        return Optional.ofNullable(handlers.get(name));
    }

}
