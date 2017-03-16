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
package org.auraframework.integration.test.root.parser;

import javax.inject.Inject;

import org.auraframework.def.ComponentDef;
import org.auraframework.def.DefDescriptor;
import org.auraframework.impl.factory.ComponentXMLParser;
import org.auraframework.impl.factory.XMLParser;
import org.auraframework.impl.source.StringSource;
import org.auraframework.system.Parser.Format;
import org.auraframework.system.TextSource;
import org.auraframework.throwable.quickfix.InvalidDefinitionException;
import org.junit.Test;

public class ComponentXMLParserTest extends XMLParserTest<ComponentDef> {
    @Inject
    private ComponentXMLParser parser;

    @Override
    protected XMLParser<ComponentDef> getParser() {
        return parser;
    }

    @Override
    protected Class<ComponentDef> getDefinitionClass() {
        return ComponentDef.class;
    }

    /**
     * Positive test: Parse a component with comments, new line character after
     * the end tag.
     * 
     * @throws Exception
     */
    @Test
    public void testParseComments() throws Exception {
        String contents = "\n<!--Comments at the beginning. New line at the begining of the file-->\n"
             +"<aura:component><!-- Comment in between tags --><aura:button label='foo' />\n"
             +"</aura:component><!-- Comment at the enf of a file -->\n";
        DefDescriptor<ComponentDef> descriptor = makeUniqueMarkupDescriptor(ComponentDef.class);
        TextSource<ComponentDef> source = makeTextSource(descriptor, contents, Format.XML);
        ComponentDef def = parser.getDefinition(descriptor, source);
        assertEquals("Unexpected Descriptor", descriptor, def.getDescriptor());
    }


    @Test
    public void testDuplicateAttributeNames() throws Exception {
        DefDescriptor<ComponentDef> descriptor = definitionService.getDefDescriptor("test:fakeparser", ComponentDef.class);
        StringSource<ComponentDef> source = new StringSource<>(
                descriptor, "<aura:component><aura:attribute name=\"implNumber\" type=\"String\"/>"
                + "<aura:attribute name=\"implNumber\" type=\"String\"/></aura:component>", "myID", Format.XML);
        ComponentDef cd = parser.getDefinition(descriptor, source);
        try {
            cd.validateDefinition();
            fail("Should have thrown Exception. Two attributes with the same name cannot exist");
        } catch (InvalidDefinitionException expected) {
            checkExceptionContains(expected, InvalidDefinitionException.class,
                    "There is already an attribute named 'implNumber' on component 'test:fakeparser'.");
        }
    }

    /**
     * An attribute cannot be assigned multiple times on a System tag.
     *
     * @throws Exception
     */
    @Test
    public void testDuplicateAttributeOnSystemTag() throws Exception {
        DefDescriptor<ComponentDef> descriptor = definitionService.getDefDescriptor("test:fakeparser", ComponentDef.class);
        StringSource<ComponentDef> source = new StringSource<>(
                descriptor,
                "<aura:component extends='test:fakeAbstract' extends='test:fakeAbstractParent'></aura:component>", "myID", Format.XML);
        ComponentDef cd = parser.getDefinition(descriptor, source);
        try {
            cd.validateDefinition();
            fail("Should have thrown Exception. Same attribute specified twice on aura:component tag.");
        } catch (InvalidDefinitionException expected) {
        }
    }

    /**
     * Verify that an attribute cannot be assigned a blank value.
     */
    @Test
    public void testBlankValueForSystemTag() throws Exception {
        DefDescriptor<ComponentDef> descriptor = definitionService.getDefDescriptor("test:fakeparser", ComponentDef.class);
        StringSource<ComponentDef> source = new StringSource<>(
                descriptor, "<aura:component extends=''></aura:component>", "myID", Format.XML);
        ComponentDef cd = parser.getDefinition(descriptor, source);
        try {
            cd.validateDefinition();
            fail("Should have thrown Exception. Attribute value cannot be blank.");
        } catch (InvalidDefinitionException expected) {
        }
    }

    @Test
    public void testHasFlavorableChildFalse() throws Exception {
        DefDescriptor<ComponentDef> descriptor = definitionService.getDefDescriptor("test:fakeparser", ComponentDef.class);
        StringSource<ComponentDef> source = new StringSource<>(
                descriptor, "<aura:component></aura:component>", "myID", Format.XML);
        ComponentDef cd = parser.getDefinition(descriptor, source);
        assertFalse(cd.hasFlavorableChild());
    }

    @Test
    public void testHasFlavorableChildTrue() throws Exception {
        DefDescriptor<ComponentDef> descriptor = definitionService.getDefDescriptor("test:fakeparser", ComponentDef.class);
        StringSource<ComponentDef> source = new StringSource<>(
                descriptor, "<aura:component><div aura:flavorable='true'></div></aura:component>", "myID", Format.XML);
        ComponentDef cd = parser.getDefinition(descriptor, source);
        assertTrue(cd.hasFlavorableChild());
    }

    @Test
    public void testHasFlavorableChildTrueNested() throws Exception {
        DefDescriptor<ComponentDef> descriptor = definitionService.getDefDescriptor("test:fakeparser", ComponentDef.class);
        StringSource<ComponentDef> source = new StringSource<>(
                descriptor, "<aura:component><div><div aura:flavorable='true'></div></div></aura:component>", "myID", Format.XML);
        ComponentDef cd = parser.getDefinition(descriptor, source);
        assertTrue(cd.hasFlavorableChild());
    }

    @Test
    public void testHasFlavorableChildTrueNestedDeep() throws Exception {
        DefDescriptor<ComponentDef> descriptor = definitionService.getDefDescriptor("test:fakeparser", ComponentDef.class);
        StringSource<ComponentDef> source = new StringSource<>(
                descriptor,
                "<aura:component><div><div><div><div aura:flavorable='true'></div></div></div></div></aura:component>", "myID", Format.XML);
        ComponentDef cd = parser.getDefinition(descriptor, source);
        assertTrue(cd.hasFlavorableChild());
    }

    @Test
    public void testInheritsFlavorableChildFalse() throws Exception {
        DefDescriptor<ComponentDef> parent = addSourceAutoCleanup(ComponentDef.class, "<aura:component extensible='true'>{!v.body}</aura:component>");
        DefDescriptor<ComponentDef> cmp = addSourceAutoCleanup(ComponentDef.class, String.format("<aura:component extends='%s'/>", parent.getDescriptorName()));
        assertFalse(definitionService.getDefinition(cmp).inheritsFlavorableChild());
    }

    @Test
    public void testInheritsFlavorableChildTrue() throws Exception {
        DefDescriptor<ComponentDef> parent = addSourceAutoCleanup(ComponentDef.class, "<aura:component extensible='true'><div aura:flavorable='true'>{!v.body}</div></aura:component>");
        DefDescriptor<ComponentDef> cmp = addSourceAutoCleanup(ComponentDef.class, String.format("<aura:component extends='%s'/>", parent.getDescriptorName()));
        assertTrue(definitionService.getDefinition(cmp).inheritsFlavorableChild());
    }

    @Test
    public void testInheritsFlavorableChildTrueDeep() throws Exception {
        DefDescriptor<ComponentDef> parent = addSourceAutoCleanup(ComponentDef.class, "<aura:component extensible='true'><div aura:flavorable='true'>{!v.body}</div></aura:component>");
        DefDescriptor<ComponentDef> parent2 = addSourceAutoCleanup(ComponentDef.class, String.format("<aura:component extends='%s' extensible='true'/>", parent.getDescriptorName()));
        DefDescriptor<ComponentDef> cmp = addSourceAutoCleanup(ComponentDef.class, String.format("<aura:component extends='%s'/>", parent2.getDescriptorName()));
        assertTrue(definitionService.getDefinition(cmp).inheritsFlavorableChild());
    }

    @Test
    public void testHasDefaultFlavor() throws Exception {
        DefDescriptor<ComponentDef> descriptor = definitionService.getDefDescriptor("test:fakeparser", ComponentDef.class);
        StringSource<ComponentDef> source = new StringSource<>(
                descriptor, "<aura:component defaultFlavor='test'></aura:component>", "myID", Format.XML);
        ComponentDef cd = parser.getDefinition(descriptor, source);
        assertEquals("test", cd.getDefaultFlavorOrImplicit());
    }

    @Test
    public void testIsDynamicallyFlavorable() throws Exception {
        DefDescriptor<ComponentDef> descriptor = definitionService.getDefDescriptor("test:fakeparser", ComponentDef.class);
        StringSource<ComponentDef> source = new StringSource<>(
                descriptor, "<aura:component dynamicallyFlavorable='true'></aura:component>", "myID", Format.XML);
        ComponentDef cd = parser.getDefinition(descriptor, source);
        assertTrue(cd.isDynamicallyFlavorable());
    }

    @Test
    public void testIsDynamicallyFlavorableInherited() throws Exception {
        DefDescriptor<ComponentDef> parent = addSourceAutoCleanup(ComponentDef.class, "<aura:component extensible='true' dynamicallyFlavorable='true'>{!v.body}</aura:component>");
        DefDescriptor<ComponentDef> parent2 = addSourceAutoCleanup(ComponentDef.class, String.format("<aura:component extends='%s' extensible='true'>{!v.body}</aura:component>", parent.getDescriptorName()));
        DefDescriptor<ComponentDef> cmp = addSourceAutoCleanup(ComponentDef.class, String.format("<aura:component extends='%s'>zell</aura:component>", parent2.getDescriptorName()));
        assertTrue(definitionService.getDefinition(cmp).isDynamicallyFlavorable());
    }
}
