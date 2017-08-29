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
package org.auraframework.impl.validation;

import org.auraframework.def.ComponentDef;
import org.auraframework.def.ControllerDef;
import org.auraframework.def.DefDescriptor;
import org.auraframework.def.StyleDef;
import org.auraframework.impl.util.AuraUtil;
import org.auraframework.util.FileMonitor;
import org.junit.Test;

import javax.inject.Inject;

import java.io.File;
import java.util.Set;

/**
 * Ugh...
 *
 * Fixme... this test should be written using something other than aura-components.
 */
public final class ValidationFileSourceLoaderTest extends AuraValidationTestCase {
    @Inject
    private FileMonitor fileMonitor;

    @Test
    public void testFindIn() {
        if (skipTestIfNotRunningWithAuraSource()) {
            return;
        }
        String basepath = AuraUtil.getAuraHome() + "/aura-components/";
        String main_components = basepath+"src/main/components/";
        String test_components = basepath+"src/test/components/";

        // find in component folder
        File root = new File(test_components+"validationTest/basic");
        ValidationFileSourceLoader loader = new ValidationFileSourceLoader(new File(test_components), fileMonitor);
        Set<DefDescriptor<?>> found = loader.findIn(root);
        assertEquals(3, found.size());
        assertTrue(found.contains(definitionService.getDefDescriptor("markup://validationTest:basic", ComponentDef.class)));
        assertTrue(found.contains(definitionService.getDefDescriptor("js://validationTest.basic", ControllerDef.class)));
        assertTrue(found.contains(definitionService.getDefDescriptor("css://validationTest.basic", StyleDef.class)));

        // find in namespace folder
        root = new File(test_components+"validationTest");
        found = loader.findIn(root);
        assertEquals(3, found.size());
        assertTrue(found.contains(definitionService.getDefDescriptor("markup://validationTest:basic", ComponentDef.class)));
        assertTrue(found.contains(definitionService.getDefDescriptor("js://validationTest.basic", ControllerDef.class)));
        assertTrue(found.contains(definitionService.getDefDescriptor("css://validationTest.basic", StyleDef.class)));

        // find in file
        root = new File(test_components+"validationTest/basic/basicController.js");
        found = loader.findIn(root);
        assertEquals(1, found.size());
        assertTrue(found.contains(definitionService.getDefDescriptor("js://validationTest.basic", ControllerDef.class)));
        root = new File(test_components+"validationTest/basic/basic.css");
        found = loader.findIn(root);
        assertEquals(1, found.size());
        assertTrue(found.contains(definitionService.getDefDescriptor("css://validationTest.basic", StyleDef.class)));

        // find nothing
        root = new File(test_components+"validationTest/basic/missing.js");
        found = loader.findIn(root);
        assertEquals(0, found.size());

        // can find aura components
        loader = new ValidationFileSourceLoader(new File(main_components), fileMonitor);
        root = new File(main_components + "ui/button/button.cmp");
        found = loader.findIn(root);
        assertEquals(1, found.size());
        assertTrue(found.contains(definitionService.getDefDescriptor("markup://ui:button", ComponentDef.class)));
    }
}
