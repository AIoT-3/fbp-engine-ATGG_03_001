package com.nhnacademy.fbp.common.parser;

import com.nhnacademy.fbp.core.flow.Flow;

import java.io.InputStream;

public interface FlowParser {
    Flow parse(InputStream in);
    Flow parse(String fileName);
}
