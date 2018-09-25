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
package org.auraframework.components.aura;

import java.io.IOException;

import javax.inject.Inject;

import org.auraframework.annotations.Annotations.ServiceComponentRenderer;
import org.auraframework.def.ComponentDefRefArray;
import org.auraframework.def.Renderer;
import org.auraframework.instance.BaseComponent;
import org.auraframework.service.RenderingService;
import org.auraframework.system.RenderContext;
import org.auraframework.throwable.quickfix.QuickFixException;
import org.springframework.context.annotation.Lazy;

/**
 * server side renderer for components that have a templated body and provide
 * their own dynamic body, e.g. iteration.cmp or if.cmp
 * 
 * @since 0.0.234
 */
@ServiceComponentRenderer
public class ProvidedBodyRenderer implements Renderer {
    @Inject
    @Lazy
    private RenderingService renderingService;

    @Override
    public void render(BaseComponent<?, ?> component, RenderContext rc) throws IOException, QuickFixException {
    	ComponentDefRefArray bodyAttribute = component.getAttributes().getValue("body", ComponentDefRefArray.class);
        if (bodyAttribute == null) {
        	return;
        }
        
        // Loop over all the items in the body, which is a ComponentDefRefArray
        // If you find a ComponentInstance, render that instance
        for (Object bodyComponent : bodyAttribute.getList()) {
            if (bodyComponent instanceof BaseComponent) {
                this.renderingService.render((BaseComponent<?, ?>) bodyComponent, rc);
        	}
        }
    }
}
