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
package org.auraframework.integration.test.root.event;

import org.auraframework.def.ComponentDef;
import org.auraframework.def.DefDescriptor;
import org.auraframework.impl.AuraImplTestCase;
import org.auraframework.impl.expression.PropertyReferenceImpl;
import org.auraframework.impl.root.event.EventHandlerDefImpl;
import org.auraframework.throwable.quickfix.DefinitionNotFoundException;
import org.auraframework.throwable.quickfix.InvalidDefinitionException;
import org.auraframework.throwable.quickfix.InvalidReferenceException;
import org.junit.Test;

public class EventHandlerDefTest extends AuraImplTestCase {
    @Test
    public void testSerialize() throws Exception {
        EventHandlerDefImpl eventHandlerDef2 = vendor.makeEventHandlerDefWithNulls(
                vendor.makeEventDefDescriptor("auratest:testevent"), new PropertyReferenceImpl("c.foo", null),
                vendor.makeLocation("filename", 5, 5, 0));
        serializeAndGoldFile(eventHandlerDef2);
    }

    /**
     * A aura:handler for a component event with event attribute specified is invalid. Should be name attribute that is
     * specified.
     */
    @Test
    public void testHandlerWithEventAttributeForComponentEvent() throws Exception {
        DefDescriptor<ComponentDef> componentDefDescriptor = definitionService.getDefDescriptor(
                "handleEventTest:handlerWithEventForComponentEvent", ComponentDef.class);
        try {
            definitionService.getDefinition(componentDefDescriptor);
            fail("Expected InvalidReferenceException");
        } catch (InvalidReferenceException e) {
            assertEquals(
                    "Incorrect exception message",
                    "A aura:handler that specifies an event=\"\" attribute must handle an application event. Either change the aura:event to have type=\"APPLICATION\" or alternately change the aura:handler to specify a name=\"\" attribute.",
                    e.getMessage());
        }
    }

    /**
     * A aura:handler for an application event with name attribute specified is invalid. Should be event attribute that
     * is specified.
     */
    @Test
    public void testHandlerWithNameAttributeForApplicationEvent() throws Exception {
        DefDescriptor<ComponentDef> componentDefDescriptor = definitionService.getDefDescriptor(
                "handleEventTest:handlerWithNameForApplicationEvent", ComponentDef.class);
        try {
            definitionService.getDefinition(componentDefDescriptor);
            fail("Expected InvalidReferenceException");
        } catch (InvalidReferenceException e) {
            assertEquals(
                    "Incorrect exception message",
                    "A aura:handler that specifies a name=\"\" attribute must handle a component event. Either change the aura:event to have type=\"COMPONENT\" or alternately change the aura:handler to specify an event=\"\" attribute.",
                    e.getMessage());
        }
    }

    @Test
    public void testHandlerWithInvalidNameAttributeForComponentEvent() throws Exception {
        DefDescriptor<ComponentDef> componentDefDescriptor = definitionService.getDefDescriptor(
                "handleEventTest:handlerWithUnregisteredName", ComponentDef.class);
        try {
            definitionService.getDefinition(componentDefDescriptor);
            fail("Expected InvalidReferenceException");
        } catch (InvalidReferenceException e) {
            assertEquals("Incorrect exception message",
                    "aura:handler has invalid name attribute value: ThisIsNotARegisteredEventName", e.getMessage());
        }
    }

    @Test
    public void testHandlerWithInvalidEventAttributeForApplicationEvent() throws Exception {
        DefDescriptor<ComponentDef> componentDefDescriptor = definitionService.getDefDescriptor(
                "handleEventTest:handlerWithInvalidEvent", ComponentDef.class);
        try {
            definitionService.getDefinition(componentDefDescriptor);
            fail("Expected DefinitionNotFoundException");
        } catch (DefinitionNotFoundException e) {
            assertEquals(
                    "Incorrect exception message",
                    String.format("No EVENT named markup://ThisIsNotAValidEventName found : [%s]",
                            componentDefDescriptor.getQualifiedName()), e.getMessage());
        }
    }

    @Test
    public void testHandlerWithEmptyNameAttributeForComponentEvent() throws Exception {
        DefDescriptor<ComponentDef> componentDefDescriptor = addSourceAutoCleanup(ComponentDef.class,
                "<aura:component><aura:handler name='' action='{!c.handleIt}'/></aura:component>");
        try {
            definitionService.getDefinition(componentDefDescriptor);
            fail("Expected InvalidReferenceException");
        } catch (InvalidReferenceException e) {
            assertEquals("Incorrect exception message", "aura:handler has invalid name attribute value: ",
                    e.getMessage());
        }
    }

    @Test
    public void testHandlerWithEmptyEventAttributeForApplicationEvent() throws Exception {
        DefDescriptor<ComponentDef> componentDefDescriptor = addSourceAutoCleanup(ComponentDef.class,
                "<aura:component><aura:handler event='' action='{!c.handleIt}'/></aura:component>");
        try {
            definitionService.getDefinition(componentDefDescriptor);
            fail("Expected InvalidDefinitionException");
        } catch (InvalidDefinitionException e) {
            assertEquals("Incorrect exception message",
                    "aura:handler must specify at least one of name=\"…\" or event=\"…\"", e.getMessage());
        }
    }

    /**
     * An aura:handler for a component event with no action parameter declared.
     */
    @Test
    public void testHandlerWithNoAction() throws Exception {
        DefDescriptor<ComponentDef> componentDefDescriptor = addSourceAutoCleanup(ComponentDef.class,
                "<aura:component><aura:registerevent name='somename' type='handleEventTest:event'/>"
                        + "<aura:handler name='somename' /></aura:component>");
        try {
            definitionService.getDefinition(componentDefDescriptor);
            fail("Expected InvalidDefinitionException");
        } catch (Exception e) {
            checkExceptionFull(e, InvalidDefinitionException.class, "aura:handler missing attribute: action=\"…\"",
                    componentDefDescriptor.getQualifiedName());
        }
    }

    @Test
    public void testGetDescription() throws Exception {
        String cmpMarkup = "<aura:component >%s</aura:component>";
        String markup = String
                .format(cmpMarkup,
                        "<aura:handler event='aura:applicationEvent' description='Describe the event handler' action='{!c.dummy}'/>");
        DefDescriptor<ComponentDef> cmpDesc = addSourceAutoCleanup(ComponentDef.class, markup);
        assertEquals("Description of registerevent not processed", "Describe the event handler",
                definitionService.getDefinition(cmpDesc).getHandlerDefs().iterator().next().getDescription());
    }
}
