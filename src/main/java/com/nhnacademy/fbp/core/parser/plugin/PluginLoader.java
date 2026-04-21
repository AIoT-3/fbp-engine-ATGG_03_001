package com.nhnacademy.fbp.core.parser.plugin;

import com.nhnacademy.fbp.node.plugin.NodeProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

@Slf4j
public final class PluginLoader {
    private final String pluginPath;

    private PluginLoader(String pluginPath) {
        this.pluginPath = pluginPath;
    }

    public static PluginLoader create(String pluginDir) {
        return new PluginLoader(pluginDir);
    }

    public Map<String, NodeProvider> getPlugins() {
        try {
            File pluginDir = new File(this.pluginPath);

            if (!pluginDir.exists()) {
                pluginDir.mkdir();
            }

            File[] plugins = pluginDir.listFiles((dir, name) -> name.endsWith(".jar"));

            if (plugins == null) return Collections.emptyMap();

            URL[] urls = new URL[plugins.length];

            for (int i = 0; i < urls.length; i++) {
                urls[i] = plugins[i].toURI().toURL();

                log.info("등록된 플러그인: {}", urls[i].toURI());
            }

            try (URLClassLoader classLoader = new URLClassLoader(urls, this.getClass().getClassLoader())) {
                ServiceLoader<NodeProvider> serviceLoader = ServiceLoader.load(NodeProvider.class, classLoader);

                return serviceLoader.stream()
                        .collect(Collectors.toMap(
                                provider -> provider.get().getNodeType(),
                                ServiceLoader.Provider::get
                        ));
            }
        } catch (Exception e) {
            log.error("플러그인 생성 중 오류 발생: {}", e.getMessage(), e);
            throw new PluginException("플러그인 생성 중 오류 발생: " + e.getMessage());
        }
    }
}
