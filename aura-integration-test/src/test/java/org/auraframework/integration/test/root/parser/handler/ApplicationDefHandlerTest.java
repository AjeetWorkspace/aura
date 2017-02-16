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
package org.auraframework.integration.test.root.parser.handler;

import javax.inject.Inject;
import javax.xml.stream.XMLStreamReader;

import org.auraframework.adapter.ConfigAdapter;
import org.auraframework.adapter.DefinitionParserAdapter;
import org.auraframework.def.ApplicationDef;
import org.auraframework.def.DefDescriptor;
import org.auraframework.def.FlavorsDef;
import org.auraframework.def.TokensDef;
import org.auraframework.impl.AuraImplTestCase;
import org.auraframework.impl.root.application.ApplicationDefImpl;
import org.auraframework.impl.root.parser.ApplicationXMLParser;
import org.auraframework.impl.root.parser.XMLParser;
import org.auraframework.impl.root.parser.handler.ApplicationDefHandler;
import org.auraframework.impl.source.StringSource;
import org.auraframework.impl.system.DefDescriptorImpl;
import org.auraframework.service.ContextService;
import org.auraframework.service.DefinitionService;
import org.auraframework.system.Parser.Format;
import org.auraframework.system.TextSource;
import org.auraframework.throwable.quickfix.InvalidDefinitionException;
import org.auraframework.throwable.quickfix.QuickFixException;
import org.junit.Test;

public class ApplicationDefHandlerTest extends AuraImplTestCase {
    @Inject
    private DefinitionParserAdapter definitionParserAdapter;
    
    XMLStreamReader xmlReader;
    ApplicationDefHandlerOverride cdHandler;

    @Inject
    private ApplicationXMLParser applicationXMLParser;

    @Inject
    DefinitionService definitionService;

    private class ApplicationDefHandlerOverride extends ApplicationDefHandler {
        public ApplicationDefHandlerOverride(DefDescriptor<ApplicationDef> applicationDefDescriptor,
                                             TextSource<ApplicationDef> source, XMLStreamReader xmlReader,
                                             boolean isInInternalNamespace, DefinitionService definitionService,
                                             ContextService contextService,
                                             ConfigAdapter configAdapter, DefinitionParserAdapter definitionParserAdapter) {
            super(applicationDefDescriptor, source, xmlReader, isInInternalNamespace, definitionService, contextService, configAdapter, definitionParserAdapter);
        }

        @Override
        public void readAttributes() throws QuickFixException {
            super.readAttributes();
        }

        @Override
        public ApplicationDefImpl createDefinition() throws QuickFixException {
            return (ApplicationDefImpl) super.createDefinition();
        }
    };

    @Override
    public void setUp() throws Exception {
        super.setUp();
        StringSource<ApplicationDef> source = new StringSource<>(
                vendor.getApplicationDefDescriptor(), "<aura:application controller='" + vendor.getControllerDescriptor().getQualifiedName() + "' extends='"
                + vendor.getParentComponentDefDescriptor() + "' implements='"
                + vendor.getInterfaceDefDescriptor()
                + "' abstract='true'>Child Text<aura:foo/></aura:application>", "myID", Format.XML);
        xmlReader = XMLParser.createXMLStreamReader(source.getHashingReader());
        xmlReader.next();
        cdHandler = new ApplicationDefHandlerOverride(vendor.getApplicationDefDescriptor(), source, xmlReader, true,
                definitionService, contextService, configAdapter, definitionParserAdapter);
    }

    @Test
    public void testReadAttributes() throws Exception {
        cdHandler.readAttributes();
        ApplicationDefImpl cd = cdHandler.createDefinition();
        assertEquals(vendor.getParentComponentDefDescriptor().getQualifiedName(), cd.getExtendsDescriptor()
                .getQualifiedName());
        assertEquals(vendor.getInterfaceDefDescriptor(), cd.getInterfaces().iterator().next());
        assertTrue(cd.isAbstract());
        assertTrue(cd.isExtensible());
    }

    @Test
    public void testGetHandledTag() {
        assertEquals("aura:application", cdHandler.getHandledTag());
    }

    @Test
    public void testDuplicateAttributeNames() throws Exception {
        DefDescriptor<ApplicationDef> descriptor = definitionService.getDefDescriptor("test:fakeparser",
                ApplicationDef.class);
        StringSource<ApplicationDef> source = new StringSource<>(
                descriptor, "<aura:application><aura:attribute name=\"implNumber\" type=\"String\"/>"
                + "<aura:attribute name=\"implNumber\" type=\"String\"/></aura:application>",
                "myID", Format.XML);
        ApplicationDef ad = applicationXMLParser.parse(descriptor, source);
        try {
            ad.validateDefinition();
            fail("Should have thrown Exception. Two attributes with the same name cannot exist");
        } catch (Exception e) {
            checkExceptionContains(e, InvalidDefinitionException.class,
                    "There is already an attribute named 'implNumber' on application 'test:fakeparser'.");
        }
    }

    @Test
    public void testReadTokenOverridesAttribute() throws QuickFixException {
        DefDescriptor<TokensDef> desc = addSourceAutoCleanup(TokensDef.class, "<aura:tokens></aura:tokens>");

        String src = String.format("<aura:application tokens=\"%s\"></aura:application>",
                desc.getDescriptorName());

        DefDescriptor<ApplicationDef> app = addSourceAutoCleanup(ApplicationDef.class, src);
        assertEquals(1, definitionService.getDefinition(app).getTokenOverrides().size());
        assertEquals(desc, definitionService.getDefinition(app).getTokenOverrides().get(0));
    }

    @Test
    public void testReadTokensAttributeMultiple() throws QuickFixException {
        DefDescriptor<TokensDef> t1 = addSourceAutoCleanup(TokensDef.class, "<aura:tokens></aura:tokens>");
        DefDescriptor<TokensDef> t2 = addSourceAutoCleanup(TokensDef.class, "<aura:tokens></aura:tokens>");
        DefDescriptor<TokensDef> t3 = addSourceAutoCleanup(TokensDef.class, "<aura:tokens></aura:tokens>");

        String src = String.format("<aura:application tokens=\"%s, %s, %s\"></aura:application>",
                t1.getDescriptorName(), t2.getDescriptorName(), t3.getDescriptorName());

        DefDescriptor<ApplicationDef> app = addSourceAutoCleanup(ApplicationDef.class, src);
        assertEquals(3, definitionService.getDefinition(app).getTokenOverrides().size());
        assertEquals(t1, definitionService.getDefinition(app).getTokenOverrides().get(0));
        assertEquals(t2, definitionService.getDefinition(app).getTokenOverrides().get(1));
        assertEquals(t3, definitionService.getDefinition(app).getTokenOverrides().get(2));
    }

    @Test
    public void testReadFlavorOverridesAttribute() throws QuickFixException {
        DefDescriptor<FlavorsDef> fa = addSourceAutoCleanup(FlavorsDef.class, "<aura:flavors></aura:flavors>");

        String src = String.format("<aura:application flavorOverrides=\"%s\"></aura:application>",
                fa.getDescriptorName());
        DefDescriptor<ApplicationDef> app = addSourceAutoCleanup(ApplicationDef.class, src);

        assertEquals(fa, definitionService.getDefinition(app).getFlavorOverrides());
    }

    @Test
    public void testFindsDefaultFlavorOverridesInBundleNoAttributeSpecified() throws QuickFixException {
        DefDescriptor<FlavorsDef> fa = addSourceAutoCleanup(FlavorsDef.class, "<aura:flavors></aura:flavors>");

        DefDescriptor<ApplicationDef> app = DefDescriptorImpl.getAssociateDescriptor(fa, ApplicationDef.class,
                DefDescriptor.MARKUP_PREFIX);
        addSourceAutoCleanup(app, String.format("<aura:application></aura:application>"));

        assertEquals(fa, definitionService.getDefinition(app).getFlavorOverrides());
    }
}
