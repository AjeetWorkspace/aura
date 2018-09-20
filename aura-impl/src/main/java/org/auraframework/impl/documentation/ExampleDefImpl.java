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
package org.auraframework.impl.documentation;

import java.io.IOException;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.auraframework.Aura;
import org.auraframework.def.ComponentDef;
import org.auraframework.def.DefDescriptor;
import org.auraframework.def.ExampleDef;
import org.auraframework.impl.system.DefinitionImpl;
import org.auraframework.throwable.quickfix.InvalidDefinitionException;
import org.auraframework.throwable.quickfix.QuickFixException;
import org.auraframework.util.json.Json;

public class ExampleDefImpl extends DefinitionImpl<ExampleDef> implements ExampleDef {

    private static final long serialVersionUID = -4467201134487458023L;

    private DefDescriptor<ComponentDef> ref;
    private String name;
    private String label;

    protected ExampleDefImpl(Builder builder) {
        super(builder);

        this.ref = builder.ref;
        this.name = builder.name;
        this.label = builder.label;
    }

    @Override
    public DefDescriptor<ComponentDef> getRef() {
        return ref;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void appendDependencies(Set<DefDescriptor<?>> dependencies) {
        super.appendDependencies(dependencies);
        dependencies.add(ref);
    }

    @Override
    public void validateDefinition() throws QuickFixException {
        super.validateDefinition();

        if (StringUtils.isBlank(name)) {
            throw new InvalidDefinitionException("<aura:example> must have attribute 'name'.", getLocation());
        }

        if (StringUtils.isBlank(label)) {
            throw new InvalidDefinitionException("<aura:example> must have attribute 'label'.", getLocation());
        }

        if (StringUtils.isBlank(description)) {
            throw new InvalidDefinitionException("<aura:example> must contain a description.", getLocation());
        }

        if (!ref.exists()) {
            throw new InvalidDefinitionException(String.format("<aura:example> reference component %s does not exist.", ref.toString()), getLocation());
        }
    }

    public static class Builder extends DefinitionImpl.BuilderImpl<ExampleDef> {

        private DefDescriptor<ComponentDef> ref;
        private String name;
        private String label;

        public Builder() {
            super(ExampleDef.class);
        }

        /**
         * @see org.auraframework.impl.system.DefinitionImpl.BuilderImpl#build()
         */
        @Override
        public ExampleDefImpl build() {
            return new ExampleDefImpl(this);
        }

        public Builder setRef(String qualifiedName) {
            this.ref = Aura.getDefinitionService().getDefDescriptor(qualifiedName, ComponentDef.class);
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setLabel(String label) {
            this.label = label;
            return this;
        }
    }

    @Override
    public void serialize(Json json) throws IOException {
        // TODO Auto-generated method stub

    }
}
