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
package org.auraframework.impl.source;

import org.auraframework.adapter.ConfigAdapter;
import org.auraframework.def.DefDescriptor;
import org.auraframework.def.Definition;
import org.auraframework.def.DescriptorFilter;
import org.auraframework.impl.util.AuraUtil;
import org.auraframework.system.InternalNamespaceSourceLoader;
import org.auraframework.system.Source;
import org.auraframework.system.SourceLoader;
import org.auraframework.throwable.AuraRuntimeException;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Locates source code and produces Source objects that can be used to read that
 * code and associated metadata. Source creation is delegated to a SourceLoader
 * that has registered to load Source for a particular namespace.
 */
public final class SourceFactory {

    /**
     * Immutable map of namespaces to loaders for this factory to use to load
     * Source.
     */
    private final Map<LoaderKey, SourceLoader> loaders;
    private static final String WILD = "*";

    private final Set<String> namespaces;

    public SourceFactory(Collection<SourceLoader> loaders, ConfigAdapter configAdapter) {
        Map<LoaderKey, SourceLoader> mutableLoaderMap = new HashMap<>();
        Set<String> mutableNamespaces = new HashSet<>();

        for (SourceLoader loader : loaders) {
            for (String namespace : loader.getNamespaces()) {
                mutableNamespaces.add(namespace);

                // Track and system/internal namespaces
                //
                // This code is rather broken, as this now assumes that we build source factories for every
                // loader. It is not fully compatible with having compiled registries, and we now have a hack
                // to make it work.
                //
                if (loader instanceof InternalNamespaceSourceLoader) {
                    InternalNamespaceSourceLoader internalLoader = (InternalNamespaceSourceLoader)loader;
                    if (internalLoader.isInternalNamespace(namespace)) {
                        String existing = configAdapter.getInternalNamespacesMap().get(namespace.toLowerCase());
                        if (existing == null) {
                            // Prevents module loaders from overriding exiting namespaces
                            // module source loaders may holder lower case namespaces of existing namespaces
                            // which it is to override so we need to keep the existing case sensitive namespace
                            // in order for modules to use existing namespaces.
                            configAdapter.addInternalNamespace(namespace);
                        }
                    }
                }

                for (String prefix : loader.getPrefixes()) {
                    LoaderKey key = new LoaderKey(namespace, prefix);
                    if (mutableLoaderMap.containsKey(key)) {
                        throw new AuraRuntimeException(String.format(
                                "Namespace/Prefix combination %s claimed by 2 SourceLoaders : %s and %s", key
                                .toString(), mutableLoaderMap.get(key).getClass().getName(), loader.getClass()
                                .getName()));
                    }

                    mutableLoaderMap.put(key, loader);
                }
            }
        }

        this.namespaces = AuraUtil.immutableSet(mutableNamespaces);
        this.loaders = AuraUtil.immutableMap(mutableLoaderMap);
    }

    public Set<String> getNamespaces() {
        return namespaces;
    }

    public <D extends Definition> Source<D> getSource(DefDescriptor<D> descriptor) {
        LoaderKey key = new LoaderKey(descriptor.getNamespace(), descriptor.getPrefix().toLowerCase());
        SourceLoader loader = loaders.get(key);
        if (loader == null) {
            return null;
        }
        return loader.getSource(descriptor);
    }

    public Set<DefDescriptor<?>> find(DescriptorFilter matcher) {
        Set<DefDescriptor<?>> ret = new HashSet<>();

        String namespace = matcher.getNamespaceMatch().toString();
        if (WILD.equals(namespace)) {
            for (String ns : namespaces) {
                String qualifiedName = String.format("%s://%s:%s",
                        matcher.getPrefixMatch(), ns, matcher.getNameMatch());
                DescriptorFilter namespacedMatcher;
                if (matcher.getDefTypes() == null) {
                    namespacedMatcher = new DescriptorFilter(qualifiedName);
                } else {
                    namespacedMatcher = new DescriptorFilter(qualifiedName, matcher.getDefTypes().get(0));
                }
                ret.addAll(find(namespacedMatcher));
            }
        } else {
            for (Map.Entry<LoaderKey, SourceLoader> entry : this.loaders.entrySet()) {
                if (matcher.matchPrefix(entry.getKey().getPrefix())
                        && matcher.matchNamespace(entry.getKey().getNamespace())) {
                    ret.addAll(entry.getValue().find(matcher));
                }
            }
        }
        return ret;
    }

    private static class LoaderKey {
        private final String namespace;
        private final String prefix;
        private final int hashCode;

        private LoaderKey(String namespace, String prefix) {
            this.namespace = namespace;
            this.prefix = prefix;
            this.hashCode = AuraUtil.hashCode(namespace != null ? namespace.toLowerCase() : null, prefix.toLowerCase());
        }

        /**
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return hashCode;
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof LoaderKey) {
                LoaderKey l = (LoaderKey) obj;
                if (prefix.equalsIgnoreCase(l.prefix) && l.namespace != null) {
                    return l.namespace.equalsIgnoreCase(namespace);
                }

            }
            return false;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return prefix + "://" + namespace;
        }

        /**
         * Gets the namespace for this instance.
         *
         * @return The namespace.
         */
        public String getNamespace() {
            return this.namespace;
        }

        /**
         * Gets the prefix for this instance.
         *
         * @return The prefix.
         */
        public String getPrefix() {
            return this.prefix;
        }
    }
}
