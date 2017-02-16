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
package org.auraframework.impl.root.parser.handler;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import org.auraframework.adapter.ConfigAdapter;
import org.auraframework.adapter.DefinitionParserAdapter;
import org.auraframework.builder.RootDefinitionBuilder;
import org.auraframework.def.DefDescriptor;
import org.auraframework.def.IncludeDefRef;
import org.auraframework.def.LibraryDef;
import org.auraframework.impl.root.library.LibraryDefImpl;
import org.auraframework.service.DefinitionService;
import org.auraframework.system.TextSource;
import org.auraframework.throwable.quickfix.QuickFixException;
import org.auraframework.util.AuraTextUtil;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import java.util.List;
import java.util.Set;

public class LibraryDefHandler extends RootTagHandler<LibraryDef> {

    public static final String TAG = "aura:library";

    private final LibraryDefImpl.Builder builder = new LibraryDefImpl.Builder();

    private final List<IncludeDefRef> includes = Lists.newArrayList();

    public LibraryDefHandler() {
        super();
    }

    public LibraryDefHandler(DefDescriptor<LibraryDef> libraryDefDescriptor, TextSource<?> source, XMLStreamReader xmlReader,
                             boolean isInInternalNamespace, DefinitionService definitionService,
                             ConfigAdapter configAdapter, DefinitionParserAdapter definitionParserAdapter) {
        super(libraryDefDescriptor, source, xmlReader, isInInternalNamespace, definitionService, configAdapter, definitionParserAdapter);
    }

    @Override
    public Set<String> getAllowedAttributes() {
        return new ImmutableSet.Builder<String>().add(RootTagHandler.ATTRIBUTE_API_VERSION)
                .addAll(super.getAllowedAttributes()).build();
    }

    @Override
    protected LibraryDefImpl createDefinition() throws QuickFixException {
        builder.setDescriptor(getDefDescriptor());
        builder.setLocation(startLocation);
        builder.setOwnHash(source.getHash());
        builder.setIncludes(includes);
        return builder.build();
    }

    @Override
    protected void handleChildTag() throws XMLStreamException, QuickFixException {
        String tag = getTagName();
        if (IncludeDefRefHandler.TAG.equals(tag)) {
            this.includes.add(new IncludeDefRefHandler(this, xmlReader, source, definitionService).getElement());
        } else {
            error("Found unexpected tag %s", tag);
        }
    }

    @Override
    public String getHandledTag() {
        return TAG;
    }

    @Override
    protected void readAttributes() throws QuickFixException {
        super.readAttributes();
        builder.setAccess(readAccessAttribute());
    }

    @Override
    protected void handleChildText() throws XMLStreamException, QuickFixException {
        String text = xmlReader.getText();
        if (!AuraTextUtil.isNullEmptyOrWhitespace(text)) {
            error("No literal text allowed in " + TAG);
        }
    }

    @Override
    protected RootDefinitionBuilder<LibraryDef> getBuilder() {
        return builder;
    }

    @Override
    protected boolean allowPrivateAttribute() {
        return true;
    }
}
