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
package org.auraframework.components.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.auraframework.def.AttributeDef;
import org.auraframework.def.ComponentDef;
import org.auraframework.def.DefDescriptor;
import org.auraframework.def.DefDescriptor.DefType;
import org.auraframework.def.DescriptorFilter;
import org.auraframework.def.EventType;
import org.auraframework.def.RegisterEventDef;
import org.auraframework.service.DefinitionService;
import org.auraframework.system.AuraContext.Authentication;
import org.auraframework.system.AuraContext.Format;
import org.auraframework.system.AuraContext.Mode;
import org.auraframework.test.util.AuraTestCase;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.annotation.Lazy;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Common tests for ui:output components
 */
public class OutputComponentsTest extends AuraTestCase {

    @Inject
    @Lazy
    private DefinitionService definitionService;

    /**
     * Verify that all ui:output* components have a required "value" attribute.
     */
    @Ignore
    @Test
    public void testValueRequired() throws Exception {
        List<String> failures = Lists.newLinkedList();
        contextService.startContext(Mode.PROD, Format.HTML, Authentication.AUTHENTICATED);
        for (ComponentDef def : getUiOutputComponents()) {
            String name = def.getName();
            AttributeDef value = def.getAttributeDef("value");
            if (value == null || !value.isRequired()) {
                failures.add(name);
            }
        }
        if (!failures.isEmpty()) {
            fail("The following ui:output* components should have a required 'value' attributef: " + failures);
        }
    }

    private Set<ComponentDef> getUiOutputComponents() throws Exception {
        DescriptorFilter matcher = new DescriptorFilter("markup://ui:output*", DefType.COMPONENT);
        Set<ComponentDef> ret = Sets.newHashSet();
        for (DefDescriptor<?> def : definitionService.find(matcher)) {
            if (def.getName().startsWith("output")) {
                ret.add((ComponentDef) definitionService.getDefinition(def));
            }
        }
        return ret;
    }

    private ComponentDef getUiOutputComponent() throws Exception {
        ComponentDef def = definitionService.getDefinition("markup://ui:output", ComponentDef.class);
        return def;
    }

    /**
     * Verify that ui:output is registered to throw all the expected events.
     * Also verify that these events are component events. Very important that
     * these events be component events and not application event.
     *
     * @throws Exception
     */
    @Test
    public void testDomEventsAreOutputComponentEvents() throws Exception {
        HashMap<String, String> events = new HashMap<>();
        events.put("blur", "markup://ui:blur");
        events.put("click", "markup://ui:click");
        events.put("dblclick", "markup://ui:dblclick");
        events.put("focus", "markup://ui:focus");
        events.put("mousedown", "markup://ui:mousedown");
        events.put("mouseup", "markup://ui:mouseup");
        events.put("mousemove", "markup://ui:mousemove");
        events.put("mouseout", "markup://ui:mouseout");
        events.put("mouseover", "markup://ui:mouseover");
        events.put("keydown", "markup://ui:keydown");
        events.put("keypress", "markup://ui:keypress");
        events.put("keyup", "markup://ui:keyup");
        events.put("select", "markup://ui:select");

        contextService.startContext(Mode.UTEST, Format.JSON, Authentication.AUTHENTICATED);
        ComponentDef def = getUiOutputComponent();
        Map<String, RegisterEventDef> registeredEvents = def.getRegisterEventDefs();

        RegisterEventDef registeredEvent;
        for (String eventName : events.keySet()) {
            registeredEvent = registeredEvents.get(eventName);
            assertNotNull("ui:output is not registered to fire event named: " + eventName, registeredEvent);
            assertEquals("Expected ui:output to throw " + events.get(eventName) + " for eventname \"" + eventName
                    + "\"", events.get(eventName), registeredEvent.getReference().getQualifiedName());
            assertEquals("Expected " + registeredEvent.getDescriptor().getQualifiedName()
                    + " event to be a component event but it is of type "
                    + definitionService.getDefinition(registeredEvent.getReference()).getEventType(),
                    EventType.COMPONENT,
                    definitionService.getDefinition(registeredEvent.getReference()).getEventType());
        }
    }

    /**
     * Verify that ui:output is not registered to throw certain events like
     * "change" that are part of DOM Events.
     */
    @Test
    public void testDomEventsWhichAreNotOutputComponentEvents() throws Exception {

        // Events which are not registered as ui:output components
        // But are part of Dom Events
        List<String> events = new ArrayList<>();
        events.add("change");

        contextService.startContext(Mode.UTEST, Format.JSON, Authentication.AUTHENTICATED);
        ComponentDef def = getUiOutputComponent();
        Map<String, RegisterEventDef> registeredEvents = def.getRegisterEventDefs();
        RegisterEventDef registeredEvent;

        Iterator<String> iterator = events.iterator();
        while (iterator.hasNext()) {
            String eventName = iterator.next().toString();
            registeredEvent = registeredEvents.get(eventName);
            assertNull("ui:output is registered to fire event named: " + eventName, registeredEvent);
        }
    }
}
