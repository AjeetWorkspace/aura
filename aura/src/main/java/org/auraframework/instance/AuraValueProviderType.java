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
package org.auraframework.instance;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

// TODO: case insensitivity for provider keys
public enum AuraValueProviderType implements ValueProviderType {
    MODEL("m"),
    VIEW("v"),
    CONTROLLER("c"),
    LABEL("$Label", true),
    BROWSER("$Browser", true),
    LOCALE("$Locale", true),
    GLOBAL("$Global", true);

    private static final Map<String, AuraValueProviderType> prefixMap;
    
    static {
        Map<String, AuraValueProviderType> m = Maps.newHashMapWithExpectedSize(values().length);
        for (AuraValueProviderType t : values()) {
            m.put(t.getPrefix(), t);
        }
        prefixMap = ImmutableMap.copyOf(m);
    }

    public static AuraValueProviderType getTypeByPrefix(String prefix) {
        return prefixMap.get(prefix);
    }

    private final String prefix;
    private final boolean global;

    private AuraValueProviderType(String prefix) {
        this(prefix, false);
    }

    private AuraValueProviderType(String prefix, boolean global) {
        this.prefix = prefix;
        this.global = global;
    }

    /**
     * @return Returns the prefix.
     */
    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public boolean isGlobal() {
        return global;
    }
}
