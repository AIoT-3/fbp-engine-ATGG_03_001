package com.nhnacademy.fbp.core.parser.plugin;

import com.nhnacademy.fbp.node.external.NodeProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PluginLoaderTest {

    @Test
    @DisplayName("플러그인 디렉토리에 플러그인 JAR 파일이 있다면, 읽어서 Map으로 변환한다.")
    void getPlugins_WhenExistsPlugins_ReturnsMap() {
        // given
        PluginLoader loader = PluginLoader.create("src/test/resources/plugin");

        // when
        Map<String, NodeProvider> plugins = loader.getPlugins();

        // then
        assertThat(plugins)
                .containsKey("HelloWorldNode");
    }

    @Test
    @DisplayName("플러그인 디렉토리가 비어 있다면, 빈 Map을 반환한다.")
    void getPlugins_WhenEmptyPlugins_ReturnsEmptyMap() {
        // given
        PluginLoader loader = PluginLoader.create("src/test/resources/empty");

        // when
        Map<String, NodeProvider> plugins = loader.getPlugins();

        // then
        assertThat(plugins)
                .isEmpty();
    }
}