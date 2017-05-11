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
package org.auraframework.components.perf;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.inject.Inject;

import org.auraframework.Aura;
import org.auraframework.annotations.Annotations.ServiceComponent;
import org.auraframework.def.DefDescriptor;
import org.auraframework.def.DefDescriptor.DefType;
import org.auraframework.def.Definition;
import org.auraframework.def.DescriptorFilter;
import org.auraframework.ds.servicecomponent.Controller;
import org.auraframework.service.DefinitionService;
import org.auraframework.system.Annotations.AuraEnabled;
import org.auraframework.system.Annotations.Key;
import org.auraframework.throwable.ClientOutOfSyncException;
import org.auraframework.throwable.quickfix.QuickFixException;
import org.auraframework.util.AuraFiles;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;

@ServiceComponent
public class DependenciesController implements Controller {
    @Inject
    private DefinitionService definitionService;


    @AuraEnabled
    public Set<String> getAllDescriptors() {
        // Note: DescriptorFilter with all DefTypes does not seem to work, so doing all separate
        DescriptorFilter matcher = new DescriptorFilter("markup://*:*", DefType.COMPONENT);
        Set<DefDescriptor<?>> descriptors = definitionService.find(matcher);

        matcher = new DescriptorFilter("markup://*:*", DefType.APPLICATION);
        descriptors.addAll(definitionService.find(matcher));
        
        matcher = new DescriptorFilter("markup://*:*", DefType.LIBRARY);
        descriptors.addAll(definitionService.find(matcher));
        
        Set<String> list = new HashSet<>();
        for (DefDescriptor<?> descriptor : descriptors) {
            list.add(descriptor.toString() + "@" + descriptor.getDefType());
        }
        
        return list;
    }
    
    
    @AuraEnabled
    public Map<String, Object> getDependencies(@Key("component")String component) {
        DefDescriptor<?> descriptor;
        SortedSet<DefDescriptor<?>> sorted;
        Map<String, Object> dependencies = Maps.newHashMap();
        ArrayList<String> list = Lists.newArrayList();
        String uid;
        
        int pos = component.indexOf("@");
        
        if (pos != -1) {
            component = component.substring(0, pos);
        }

        DescriptorFilter filter = new DescriptorFilter(component,
                Lists.newArrayList(DefType.LIBRARY,DefType.COMPONENT,DefType.APPLICATION));
        Set<DefDescriptor<?>> descriptors = definitionService.find(filter);
        if (descriptors.size() != 1) {
            return null;
        }
        descriptor = descriptors.iterator().next();
        
        try {
            Definition def = definitionService.getDefinition(descriptor);
            if (def == null) {
                return null;
            }
            
            descriptor = def.getDescriptor();
            uid = definitionService.getUid(null, descriptor);
            sorted = Sets.newTreeSet(definitionService.getDependencies(uid));
            
            for (DefDescriptor<?> dep : sorted) {
                def = definitionService.getDefinition(dep);
                DefType type = dep.getDefType();
                
                if (type != DefType.EVENT && type != DefType.COMPONENT && type != DefType.INTERFACE && type != DefType.LIBRARY || 
                    (def.getDescriptor().getNamespace().equals("aura") || def.getDescriptor().getNamespace().equals("auradev"))) {
                    continue;
                }
                
                list.add(dep.toString() + "@" + dep.getDefType());
            }
            
        dependencies.put("dependencies", list);
        dependencies.put("def", component);
        
        return dependencies;    
            
        } catch (Throwable t) {
            return null;
        }
    }
    
    /**
     * Used by the traceDependencies.app application.
     * Returns a similar set of data to getDependencies method, but also includes UIDs and HashCodes which help us diagnose 
     * COOS issues.
     * 
     * @param component
     * @return
     */
    @AuraEnabled
    public Map<String, Object> getDependenciesWithHashCodes(@Key("component")String component) {
        DefDescriptor<?> descriptor;
        SortedSet<DefDescriptor<?>> sorted;
        Map<String, Object> dependencies = Maps.newHashMap();
        ArrayList<Map<String, String>> dependenciesData = Lists.newArrayList();
        String uid;
    	
        int pos = component.indexOf("@");
        
        if (pos != -1) {
            component = component.substring(0, pos);
        }

        DescriptorFilter filter = new DescriptorFilter(component,
                Lists.newArrayList(DefType.LIBRARY,DefType.COMPONENT,DefType.APPLICATION,DefType.FLAVORED_STYLE));
        Set<DefDescriptor<?>> descriptors = definitionService.find(filter);
        if (descriptors.size() != 1) {
            return null;
        }
        descriptor = descriptors.iterator().next();
        
        try {
            Definition definition = definitionService.getDefinition(descriptor);
            if (definition == null) {
                return null;
            }
            
            descriptor = definition.getDescriptor();
            uid = definitionService.getUid(null, descriptor);
            sorted = Sets.newTreeSet(definitionService.getDependencies(uid));
            
            for (DefDescriptor<?> dependency : sorted) {
                definition = definitionService.getDefinition(dependency);
                DefType type = dependency.getDefType();
                
                Map<String, String> returnData = Maps.newHashMap();
                
                try {
                	String bundle = dependency.getBundle() != null ? dependency.getBundle().getQualifiedName() : "";
                	returnData.put("descriptor", dependency.toString());
                	returnData.put("defType", type.toString());
                	returnData.put("uid", definitionService.getUid(null, dependency));
                	returnData.put("bundleName", bundle);
                	returnData.put("hash", definitionService.getDefinition(dependency).getOwnHash());
                	
            	} catch(ClientOutOfSyncException | QuickFixException ex) {
            	    returnData.put("descriptor", dependency.toString());
            	    returnData.put("defType", type.toString());
            	    returnData.put("error", ex.getMessage());
            	    
            	}
                
                dependenciesData.add(returnData);
            }
            
            dependencies.put("dependencies", dependenciesData);
            dependencies.put("def", component);
            
            return dependencies;    
            
        } catch (Throwable t) {
            return null;
        }
    }
    
    @AuraEnabled
    public Boolean writeAllDependencies(@Key("file")String file) {
        Set<String> descriptors = getAllDescriptors();
        Map<String, Object> dependencies = Maps.newHashMap();
   
            for (String rawDescriptor : descriptors) {
                Map<String, Object> list = getDependencies(rawDescriptor);
                if (list != null) {
                    list.remove("def");
                }
                dependencies.put(rawDescriptor, list);
            }

            Gson gson = new Gson(); 
            String json = gson.toJson(dependencies);
            String path = AuraFiles.Core.getPath() + "/aura-resources/src/main/resources/aura/resources/";
            try(PrintWriter out = new PrintWriter( path + file )) {
                out.println(json);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        return true;
    }
}
