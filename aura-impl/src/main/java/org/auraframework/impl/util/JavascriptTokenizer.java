/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.auraframework.impl.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.auraframework.builder.JavascriptCodeBuilder;
import org.auraframework.impl.expression.PropertyReferenceImpl;
import org.auraframework.impl.root.parser.handler.DependencyDefHandler;
import org.auraframework.system.Location;
import org.auraframework.throwable.quickfix.QuickFixException;
import org.auraframework.util.AuraTextUtil;

/**
 * Parses JavaScript to extract labels and dependencies.
 */
public class JavascriptTokenizer {

    private static final Pattern LABEL_GVP_PATTERN = Pattern.compile("(\\$Label\\.\\w+\\.\\w+)",
            Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    private static final Pattern QUALIFIED_NAME_PATTERN = Pattern.compile("(markup://\\w+:\\w+)",
            Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    private final String code;
    private final Location location;

    public JavascriptTokenizer(String code, Location location) {
        this.code = code;
        this.location = location;
    }

    public void process(JavascriptCodeBuilder builder) {
        if (!AuraTextUtil.isNullEmptyOrWhitespace(code)) {
            parseLabels(builder);
            addDependencies(builder);
        }
    }

    /**
     * Parse all labels GVPs from the source code and add the expressions on the builder.
     */
    private void parseLabels(JavascriptCodeBuilder builder)  {
        Matcher matcher = LABEL_GVP_PATTERN.matcher(code);
        while (matcher.find()) {

            String labelRef = matcher.group();

            builder.addExpressionRef(new PropertyReferenceImpl(labelRef, location));
        }
    }

    /**
     * Parse all qualified descriptor names from the source code and add dependencies on the builder.
     */
    private void addDependencies(JavascriptCodeBuilder builder)  {
        Matcher matcher = QUALIFIED_NAME_PATTERN.matcher(code);
        while (matcher.find()) {
            //
            // JS can only get "root" definitions, minus applications, as applications can only be loaded
            // as a "top level" load. Libraries could arguably be excluded.
            //
            try {
                builder.addDependency(DependencyDefHandler.generateFilter(null, matcher.group(), 
                                                        "COMPONENT,INTERFACE,EVENT,LIBRARY,MODULE", location));
            } catch (QuickFixException qfe) {
                // ignore dependencies that can't be parsed
            }
        }
    }
}
