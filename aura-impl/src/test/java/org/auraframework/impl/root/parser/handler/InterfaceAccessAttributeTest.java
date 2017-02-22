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

import javax.inject.Inject;

import org.auraframework.def.DefDescriptor;
import org.auraframework.def.InterfaceDef;
import org.auraframework.impl.AuraImplTestCase;
import org.auraframework.service.CompilerService;
import org.auraframework.system.TextSource;
import org.auraframework.test.source.StringSourceLoader;
import org.auraframework.test.source.StringSourceLoader.NamespaceAccess;
import org.auraframework.throwable.quickfix.InvalidAccessValueException;
import org.auraframework.util.test.annotation.UnAdaptableTest;
import org.junit.Test;


@UnAdaptableTest("when run in core, we throw error with different type.")
public class InterfaceAccessAttributeTest extends AuraImplTestCase {

    @Inject
    private CompilerService compilerService;

	/***********************************************************************************
     ******************* Tests for Internal Namespace start ****************************
     ************************************************************************************/
    
    //testDefaultAccess
    @Test
    public void testInterfaceWithDefaultAccessInternalNamespace() throws Exception {
        String intfSource = "<aura:interface/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_NAMESPACE+":testInterface",
                        NamespaceAccess.INTERNAL);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    
    //testEmptyAccess
    @Test
    public void testInterfaceWithEmptyAccessInternalNamespace() throws Exception {
        String intfSource = "<aura:interface access=''/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_NAMESPACE+":testInterface",
                        NamespaceAccess.INTERNAL);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    //testInvalidAccess
    @Test
    public void testInterfaceWithInvalidAccessInternalNamespace() throws Exception {
        String intfSource = "<aura:interface access='BLAH'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_NAMESPACE+":testInterface",
                        NamespaceAccess.INTERNAL);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"BLAH\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    //testInvalidAccessDynamic
    @Test
    public void testInterfaceWithInvalidAccessMethodInternalNamespace() throws Exception {
        String intfSource = "<aura:interface access='org.auraframework.impl.test.util.TestAccessMethods.invalid'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_NAMESPACE+":testInterface",
                        NamespaceAccess.INTERNAL);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "\"org.auraframework.impl.test.util.TestAccessMethods.invalid\" must return a result of type org.auraframework.system.AuraContext$Access";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    //testInvalidValidAccess
    //we can have valid access mix with invalid access, it will error out on the invalid one
    @Test
    public void testInterfaceWithInvalidAndValidAccessInternalNamespace() throws Exception {
        String intfSource = "<aura:interface access='GLOBAL, BLAH, GLOBAL'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_NAMESPACE+":testInterface",
                        NamespaceAccess.INTERNAL);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"BLAH\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    //testAccessValueAndStaticMethod
    @Test
    public void testInterfaceWithStaticAccessAndAccessMethodInternalNamespace() throws Exception {
        String intfSource = "<aura:interface access='GLOBAL,org.auraframework.impl.test.util.TestAccessMethods.allowGlobal' />";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_NAMESPACE+":testInterface",
                    NamespaceAccess.INTERNAL);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Access attribute may not specify \"GLOBAL\" when a static method is also specified";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    //testStaticMethodAndAuthentication
    @Test
    public void testInterfaceWithAuthenticationAndAccessMethodInternalNamespace() throws Exception {
        String intfSource = "<aura:interface access='org.auraframework.impl.test.util.TestAccessMethods.allowGlobal,AUTHENTICATED'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_NAMESPACE+":testInterface",
                        NamespaceAccess.INTERNAL);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"AUTHENTICATED\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    /*
     * testSimpleAccessInSystemNamespace
     * go through access=GLOBAL,PUBLIC,PRIVATE,INTERNAL,PRIVILEGED for interface in InternalNamespace
     * only PRIVATE is invalid
     */
    @Test
    public void testInterfaceWithGlobalAccessInternalNamespace() throws Exception {
        String intfSource = "<aura:interface access='GLOBAL'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_NAMESPACE+":testInterface",
                        NamespaceAccess.INTERNAL);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    @Test
    public void testInterfaceWithPublicAccessInternalNamespace() throws Exception {
        String intfSource = "<aura:interface access='PUBLIC'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_NAMESPACE+":testInterface",
                        NamespaceAccess.INTERNAL);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    //the only invalid case among 5
    @Test
    public void testInterfaceWithPrivateAccessInternalNamespace() throws Exception {
        String intfSource = "<aura:interface access='PRIVATE'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_NAMESPACE+":testInterface",
                        NamespaceAccess.INTERNAL);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"PRIVATE\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    @Test
    public void testInterfaceWithInternalAccessInternalNamespace() throws Exception {
        String intfSource = "<aura:interface access='INTERNAL'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_NAMESPACE+":testInterface",
                        NamespaceAccess.INTERNAL);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    @Test
    public void testInterfaceWithPrivilegedAccessInternalNamespace() throws Exception {
        String intfSource = "<aura:interface access='PRIVILEGED'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_NAMESPACE+":testInterface",
                        NamespaceAccess.INTERNAL);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    
    /*
     * testSimpleAccessDynamicInSystemNamespace
     * go through access=GLOBAL,PUBLIC,PRIVATE,INTERNAL,PRIVILEGED by AccessMethod for interface in InternalNamespace
     * notice that we only allow access method within internalNamespace.
     */
    @Test
    public void testInterfaceWithGlobalAccessMethodInternalNamespace() throws Exception {
        String intfSource = "<aura:interface access='org.auraframework.impl.test.util.TestAccessMethods.allowGlobal'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_NAMESPACE+":testInterface",
                        NamespaceAccess.INTERNAL);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    @Test
    public void testInterfaceWithPublicAccessMethodInternalNamespace() throws Exception {
        String intfSource = "<aura:interface access='org.auraframework.impl.test.util.TestAccessMethods.allowPublic'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_NAMESPACE+":testInterface",
                        NamespaceAccess.INTERNAL);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    //TODO: whey private is ok here?
    @Test
    public void testInterfaceWithPrivateAccessMethodInternalNamespace() throws Exception {
        String intfSource = "<aura:interface access='org.auraframework.impl.test.util.TestAccessMethods.allowPrivate'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_NAMESPACE+":testInterface",
                        NamespaceAccess.INTERNAL);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    @Test
    public void testInterfaceWithPrivilegedAccessMethodInternalNamespace() throws Exception {
        String intfSource = "<aura:interface access='org.auraframework.impl.test.util.TestAccessMethods.allowPrivileged'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_NAMESPACE+":testInterface",
                        NamespaceAccess.INTERNAL);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    @Test
    public void testInterfaceWithInternalAccessMethodInternalNamespace() throws Exception {
        String intfSource = "<aura:interface access='org.auraframework.impl.test.util.TestAccessMethods.allowInternal'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_NAMESPACE+":testInterface",
                        NamespaceAccess.INTERNAL);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    
    
    /*
     * testCombinationAccessInSystemNamespace
     * this verify putting any two different valid access value together won't work. you can try other combinations, but it's the same
     */
    @Test
    public void testInterfaceWithGlobalAndPrivateAccessInternalNamespace() throws Exception {
        String intfSource = "<aura:interface access='GLOBAL, PRIVATE'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_NAMESPACE+":testInterface",
                        NamespaceAccess.INTERNAL);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Access attribute can only specify one of GLOBAL, PUBLIC, or PRIVATE";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    /*
     * testCombinationAccessInSystemNamespace
     * this verify we can put two same valid access value together 
     */
    @Test
    public void testInterfaceWithPublicAndPublicAccessInternalNamespace() throws Exception {
        String intfSource = "<aura:interface access='PUBLIC, PUBLIC'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_NAMESPACE+":testInterface",
                        NamespaceAccess.INTERNAL);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    

    /*
     * testSimpleAuthenticationInSystemNamespace
     */
    @Test
    public void testInterfaceWithAuthenticationInternalNamespace() throws Exception {
        String intfSource = "<aura:interface access='AUTHENTICATED'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_NAMESPACE+":testInterface",
                        NamespaceAccess.INTERNAL);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"AUTHENTICATED\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    @Test
    public void testInterfaceWithUnAuthenticationInternalNamespace() throws Exception {
        String intfSource = "<aura:interface access='UNAUTHENTICATED'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_NAMESPACE+":testInterface",
                        NamespaceAccess.INTERNAL);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"UNAUTHENTICATED\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    
    /*
     * testSimpleAuthenticationDynamicInSystemNamespace
     * we cannot set access to Authenticated by method, like what we do for access value
     */
    @Test
    public void testInterfaceWithUnAuthenticationMethodInternalNamespace() throws Exception {
        String intfSource = "<aura:interface access='org.auraframework.impl.test.util.TestAccessMethods.allowAuthenticated'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_NAMESPACE+":testInterface",
                        NamespaceAccess.INTERNAL);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "\"org.auraframework.impl.test.util.TestAccessMethods.allowAuthenticated\" must return a result of type org.auraframework.system.AuraContext$Access";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    /*
     * testCombinationAuthenticationInSystemNamespace
     * this verify we cannot have both AUTHENTICATED and UNAUTHENTICATED as access attribute
     */
    @Test
    public void testInterfaceWithAuthenticatedAndUnAuthenticationAccessInternalNamespace() throws Exception {
        String intfSource = "<aura:interface access='AUTHENTICATED,UNAUTHENTICATED'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_NAMESPACE+":testInterface",
                        NamespaceAccess.INTERNAL);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Access attribute cannot specify both AUTHENTICATED and UNAUTHENTICATED";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    /*
     * This verify we can have same Authentication as access attribute
     */
    @Test
    public void testInterfaceWithAuthenticatedAndAuthenticatedAccessInternalNamespace() throws Exception {
        String intfSource = "<aura:interface access='AUTHENTICATED,AUTHENTICATED'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_NAMESPACE+":testInterface",
                        NamespaceAccess.INTERNAL);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"AUTHENTICATED\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    /*
     * testAccessAuthenticationInSystemNamespace
     * These verify we can have both authentication and valid static access (GLOBAL,PUBLIC,PRIVILEGED,INTERNAL). 
     * notice : for interface, we cannot have authentication in access attribute
     */
    @Test
    public void testInterfaceWithAuthenticatedAndGlobalAccessInternalNamespace() throws Exception {
        String intfSource = "<aura:interface access='AUTHENTICATED,GLOBAL'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_NAMESPACE+":testInterface",
                        NamespaceAccess.INTERNAL);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"AUTHENTICATED\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    @Test
    public void testInterfaceWithAuthenticatedAndPrivateAccessInternalNamespace() throws Exception {
        String intfSource = "<aura:interface access='AUTHENTICATED,PRIVATE'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_NAMESPACE+":testInterface",
                        NamespaceAccess.INTERNAL);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"AUTHENTICATED\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    @Test
    public void testInterfaceWithAuthenticatedAndPublicAccessInternalNamespace() throws Exception {
        String intfSource = "<aura:interface access='AUTHENTICATED,PUBLIC'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_NAMESPACE+":testInterface",
                        NamespaceAccess.INTERNAL);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"AUTHENTICATED\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    @Test
    public void testInterfaceWithAuthenticatedAndPrivilegedAccessInternalNamespace() throws Exception {
        String intfSource = "<aura:interface access='AUTHENTICATED,PRIVILEGED'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_NAMESPACE+":testInterface",
                        NamespaceAccess.INTERNAL);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"AUTHENTICATED\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    @Test
    public void testInterfaceWithAuthenticatedAndInternalAccessInternalNamespace() throws Exception {
        String intfSource = "<aura:interface access='AUTHENTICATED,INTERNAL'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_NAMESPACE+":testInterface",
                        NamespaceAccess.INTERNAL);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"AUTHENTICATED\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    
    /***********************************************************************************
     ******************* Tests for Privileged Namespace start ****************************
     ************************************************************************************/
    
    /*
     * testSimpleAccessInPrivilegedNamespace
     */
    @Test
    public void testInterfaceWithDefaultAccessPrivilegedNamespace() throws Exception {
        String intfSource = "<aura:interface/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    
    //testEmptyAccess
    @Test
    public void testInterfaceWithEmptyAccessPrivilegedNamespace() throws Exception {
        String intfSource = "<aura:interface access=''/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    //testInvalidAccess
    @Test
    public void testInterfaceWithInvalidAccessPrivilegedNamespace() throws Exception {
        String intfSource = "<aura:interface access='BLAH'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"BLAH\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    //testInvalidAccessDynamic
    @Test
    public void testInterfaceWithInvalidAccessMethodPrivilegedNamespace() throws Exception {
        String intfSource = "<aura:interface access='org.auraframework.impl.test.util.TestAccessMethods.invalid'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "\"org.auraframework.impl.test.util.TestAccessMethods.invalid\" must return a result of type org.auraframework.system.AuraContext$Access";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    //testInvalidValidAccess
    //we can have valid access mix with invalid access, it will error out on the invalid one
    @Test
    public void testInterfaceWithInvalidAndValidAccessPrivilegedNamespace() throws Exception {
        String intfSource = "<aura:interface access='GLOBAL, BLAH, GLOBAL'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"BLAH\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    //testAccessValueAndStaticMethod
    @Test
    public void testInterfaceWithStaticAccessAndAccessMethodPrivilegedNamespace() throws Exception {
        String intfSource = "<aura:interface access='GLOBAL,org.auraframework.impl.test.util.TestAccessMethods.allowGlobal' />";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                    NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Access attribute may not specify \"GLOBAL\" when a static method is also specified";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    //testStaticMethodAndAuthentication
    @Test
    public void testInterfaceWithAuthenticationAndAccessMethodPrivilegedNamespace() throws Exception {
        String intfSource = "<aura:interface access='org.auraframework.impl.test.util.TestAccessMethods.allowGlobal,AUTHENTICATED'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"AUTHENTICATED\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    /*
     * testSimpleAccessInSystemNamespace
     * go through access=GLOBAL,PUBLIC,PRIVATE,INTERNAL,PRIVILEGED for interface in InternalNamespace
     * INTERNAL and PRIVATE are invalid
     */
    @Test
    public void testInterfaceWithGlobalAccessPrivilegedNamespace() throws Exception {
        String intfSource = "<aura:interface access='GLOBAL'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    @Test
    public void testInterfaceWithPublicAccessPrivilegedNamespace() throws Exception {
        String intfSource = "<aura:interface access='PUBLIC'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    @Test
    public void testInterfaceWithPrivateAccessPrivilegedNamespace() throws Exception {
        String intfSource = "<aura:interface access='PRIVATE'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"PRIVATE\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    @Test
    public void testInterfaceWithInternalAccessPrivilegedNamespace() throws Exception {
        String intfSource = "<aura:interface access='INTERNAL'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"INTERNAL\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    @Test
    public void testInterfaceWithPrivilegedAccessPrivilegedNamespace() throws Exception {
        String intfSource = "<aura:interface access='PRIVILEGED'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    
    /*
     * testSimpleAccessDynamicInSystemNamespace
     * go through access=GLOBAL,PUBLIC,PRIVATE,INTERNAL,PRIVILEGED by AccessMethod for interface in PrivilegedNamespace
     */
    @Test
    public void testInterfaceWithGlobalAccessMethodPrivilegedNamespace() throws Exception {
        String intfSource = "<aura:interface access='org.auraframework.impl.test.util.TestAccessMethods.allowGlobal'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Access attribute may not use a static method";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    @Test
    public void testInterfaceWithPublicAccessMethodPrivilegedNamespace() throws Exception {
        String intfSource = "<aura:interface access='org.auraframework.impl.test.util.TestAccessMethods.allowPublic'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Access attribute may not use a static method";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    @Test
    public void testInterfaceWithPrivateAccessMethodPrivilegedNamespace() throws Exception {
        String intfSource = "<aura:interface access='org.auraframework.impl.test.util.TestAccessMethods.allowPrivate'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Access attribute may not use a static method";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    @Test
    public void testInterfaceWithPrivilegedAccessMethodPrivilegedNamespace() throws Exception {
        String intfSource = "<aura:interface access='org.auraframework.impl.test.util.TestAccessMethods.allowPrivileged'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Access attribute may not use a static method";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    @Test
    public void testInterfaceWithInternalAccessMethodPrivilegedNamespace() throws Exception {
        String intfSource = "<aura:interface access='org.auraframework.impl.test.util.TestAccessMethods.allowInternal'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Access attribute may not use a static method";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    
    /*
     * testCombinationAccessInSystemNamespace
     * this verify putting any two different valid access value together won't work. you can try other combinations, but it's the same
     */
    @Test
    public void testInterfaceWithGlobalAndPrivateAccessPrivilegedNamespace() throws Exception {
        String intfSource = "<aura:interface access='GLOBAL, PRIVATE'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Access attribute can only specify one of GLOBAL, PUBLIC, or PRIVATE";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    /*
     * testCombinationAccessInSystemNamespace
     * this verify we can put two same valid access value together 
     */
    @Test
    public void testInterfaceWithPublicAndPublicAccessPrivilegedNamespace() throws Exception {
        String intfSource = "<aura:interface access='PUBLIC, PUBLIC'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    

    /*
     * testSimpleAuthenticationInSystemNamespace, we cannot have authentication as access, as we are not in InternalNamespace
     */
    @Test
    public void testInterfaceWithAuthenticationPrivilegedNamespace() throws Exception {
        String intfSource = "<aura:interface access='AUTHENTICATED'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"AUTHENTICATED\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    @Test
    public void testInterfaceWithUnAuthenticationPrivilegedNamespace() throws Exception {
        String intfSource = "<aura:interface access='UNAUTHENTICATED'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"UNAUTHENTICATED\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    
    /*
     * testSimpleAuthenticationDynamicInSystemNamespace
     * we cannot set access to Authenticated by method, like what we do for access value
     */
    @Test
    public void testInterfaceWithUnAuthenticationMethodPrivilegedNamespace() throws Exception {
        String intfSource = "<aura:interface access='org.auraframework.impl.test.util.TestAccessMethods.allowAuthenticated'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "\"org.auraframework.impl.test.util.TestAccessMethods.allowAuthenticated\" must return a result of type org.auraframework.system.AuraContext$Access";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    /*
     * testCombinationAuthenticationInSystemNamespace
     * this verify we cannot have both AUTHENTICATED and UNAUTHENTICATED as access attribute
     */
    @Test
    public void testInterfaceWithAuthenticatedAndUnAuthenticationAccessPrivilegedNamespace() throws Exception {
        String intfSource = "<aura:interface access='AUTHENTICATED,UNAUTHENTICATED'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Access attribute cannot specify both AUTHENTICATED and UNAUTHENTICATED";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    /*
     * This verify we cannot have Authentication as access attribute, as we are outside InternalNamespace
     */
    @Test
    public void testInterfaceWithAuthenticatedAndAuthenticationAccessPrivilegedNamespace() throws Exception {
        String intfSource = "<aura:interface access='AUTHENTICATED,AUTHENTICATED'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"AUTHENTICATED\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    /*
     * testAccessAuthenticationInPrivilegedNamespace
     * These verify we cannot have authentication as part of access, as we are not in InternalNamespace
     */
    @Test
    public void testInterfaceWithAuthenticatedAndGlobalAccessPrivilegedNamespace() throws Exception {
        String intfSource = "<aura:interface access='AUTHENTICATED,GLOBAL'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"AUTHENTICATED\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    @Test
    public void testInterfaceWithAuthenticatedAndPrivateAccessPrivilegedNamespace() throws Exception {
        String intfSource = "<aura:interface access='AUTHENTICATED,PRIVATE'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"AUTHENTICATED\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    @Test
    public void testInterfaceWithAuthenticatedAndPublicAccessPrivilegedNamespace() throws Exception {
        String intfSource = "<aura:interface access='AUTHENTICATED,PUBLIC'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"AUTHENTICATED\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    @Test
    public void testInterfaceWithAuthenticatedAndPrivilegedAccessPrivilegedNamespace() throws Exception {
        String intfSource = "<aura:interface access='AUTHENTICATED,PRIVILEGED'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"AUTHENTICATED\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    @Test
    public void testInterfaceWithAuthenticatedAndInternalAccessPrivilegedNamespace() throws Exception {
        String intfSource = "<aura:interface access='AUTHENTICATED,INTERNAL'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"AUTHENTICATED\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    
    
    
    
    /***********************************************************************************
     ****** Tests for Custom (non-internal, non-privileged) Namespace start ************
     ************************************************************************************/
    
    /*
     * testSimpleAccessInPrivilegedNamespace
     */
    @Test
    public void testInterfaceWithDefaultAccessCustomNamespace() throws Exception {
        String intfSource = "<aura:interface/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_CUSTOM_NAMESPACE+":testInterface",
                        NamespaceAccess.CUSTOM);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    
    //testEmptyAccess
    @Test
    public void testInterfaceWithEmptyAccessCustomNamespace() throws Exception {
        String intfSource = "<aura:interface access=''/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_CUSTOM_NAMESPACE+":testInterface",
                        NamespaceAccess.CUSTOM);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    //testInvalidAccess
    @Test
    public void testInterfaceWithInvalidAccessCustomNamespace() throws Exception {
        String intfSource = "<aura:interface access='BLAH'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_CUSTOM_NAMESPACE+":testInterface",
                        NamespaceAccess.CUSTOM);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"BLAH\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    //testInvalidAccessDynamic
    @Test
    public void testInterfaceWithInvalidAccessMethodCustomNamespace() throws Exception {
        String intfSource = "<aura:interface access='org.auraframework.impl.test.util.TestAccessMethods.invalid'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_CUSTOM_NAMESPACE+":testInterface",
                        NamespaceAccess.CUSTOM);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "\"org.auraframework.impl.test.util.TestAccessMethods.invalid\" must return a result of type org.auraframework.system.AuraContext$Access";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    //testInvalidValidAccess
    //we can have valid access mix with invalid access, it will error out on the invalid one
    @Test
    public void testInterfaceWithInvalidAndValidAccessCustomNamespace() throws Exception {
        String intfSource = "<aura:interface access='GLOBAL, BLAH, GLOBAL'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_CUSTOM_NAMESPACE+":testInterface",
                        NamespaceAccess.CUSTOM);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"BLAH\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    //testAccessValueAndStaticMethod
    @Test
    public void testInterfaceWithStaticAccessAndAccessMethodCustomNamespace() throws Exception {
        String intfSource = "<aura:interface access='GLOBAL,org.auraframework.impl.test.util.TestAccessMethods.allowGlobal' />";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_CUSTOM_NAMESPACE+":testInterface",
                    NamespaceAccess.CUSTOM);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Access attribute may not specify \"GLOBAL\" when a static method is also specified";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    //testStaticMethodAndAuthentication
    @Test
    public void testInterfaceWithAuthenticationAndAccessMethodCustomNamespace() throws Exception {
        String intfSource = "<aura:interface access='org.auraframework.impl.test.util.TestAccessMethods.allowGlobal,AUTHENTICATED'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_CUSTOM_NAMESPACE+":testInterface",
                        NamespaceAccess.CUSTOM);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"AUTHENTICATED\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    /*
     * go through access=GLOBAL,PUBLIC,PRIVATE,INTERNAL,PRIVILEGED for interface in CustomNamespace
     * PRIVATE, INTERNAL and PRIVILEGED are not valid
     */
    @Test
    public void testInterfaceWithGlobalAccessCustomNamespace() throws Exception {
        String intfSource = "<aura:interface access='GLOBAL'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_CUSTOM_NAMESPACE+":testInterface",
                        NamespaceAccess.CUSTOM);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    @Test
    public void testInterfaceWithPublicAccessCustomNamespace() throws Exception {
        String intfSource = "<aura:interface access='PUBLIC'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    @Test
    public void testInterfaceWithPrivateAccessCustomNamespace() throws Exception {
        String intfSource = "<aura:interface access='PRIVATE'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_CUSTOM_NAMESPACE+":testInterface",
                        NamespaceAccess.CUSTOM);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"PRIVATE\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    @Test
    public void testInterfaceWithInternalAccessCustomNamespace() throws Exception {
        String intfSource = "<aura:interface access='INTERNAL'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_CUSTOM_NAMESPACE+":testInterface",
                        NamespaceAccess.CUSTOM);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"INTERNAL\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    @Test
    public void testInterfaceWithPrivilegedAccessCustomNamespace() throws Exception {
        String intfSource = "<aura:interface access='PRIVILEGED'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_CUSTOM_NAMESPACE+":testInterface",
                        NamespaceAccess.CUSTOM);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"PRIVILEGED\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    /*
     * go through access=GLOBAL,PUBLIC,PRIVATE,INTERNAL,PRIVILEGED by AccessMethod for interface in CustomNamespace
     */
    @Test
    public void testInterfaceWithGlobalAccessMethodCustomNamespace() throws Exception {
        String intfSource = "<aura:interface access='org.auraframework.impl.test.util.TestAccessMethods.allowGlobal'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_CUSTOM_NAMESPACE+":testInterface",
                        NamespaceAccess.CUSTOM);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Access attribute may not use a static method";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    @Test
    public void testInterfaceWithPublicAccessMethodCustomNamespace() throws Exception {
        String intfSource = "<aura:interface access='org.auraframework.impl.test.util.TestAccessMethods.allowPublic'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_CUSTOM_NAMESPACE+":testInterface",
                        NamespaceAccess.CUSTOM);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Access attribute may not use a static method";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    @Test
    public void testInterfaceWithPrivateAccessMethodCustomNamespace() throws Exception {
        String intfSource = "<aura:interface access='org.auraframework.impl.test.util.TestAccessMethods.allowPrivate'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_CUSTOM_NAMESPACE+":testInterface",
                        NamespaceAccess.CUSTOM);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Access attribute may not use a static method";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    @Test
    public void testInterfaceWithPrivilegedAccessMethodCustomNamespace() throws Exception {
        String intfSource = "<aura:interface access='org.auraframework.impl.test.util.TestAccessMethods.allowPrivileged'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_CUSTOM_NAMESPACE+":testInterface",
                        NamespaceAccess.CUSTOM);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Access attribute may not use a static method";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    @Test
    public void testInterfaceWithInternalAccessMethodCustomNamespace() throws Exception {
        String intfSource = "<aura:interface access='org.auraframework.impl.test.util.TestAccessMethods.allowInternal'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_CUSTOM_NAMESPACE+":testInterface",
                        NamespaceAccess.CUSTOM);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        
        String expectedMsg = "Access attribute may not use a static method";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    
    /*
     * this verify putting any two different valid access value together won't work. you can try other combinations, but it's the same
     */
    @Test
    public void testInterfaceWithGlobalAndPrivateAccessCustomNamespace() throws Exception {
        String intfSource = "<aura:interface access='GLOBAL, PRIVATE'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_CUSTOM_NAMESPACE+":testInterface",
                        NamespaceAccess.CUSTOM);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Access attribute can only specify one of GLOBAL, PUBLIC, or PRIVATE";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    /*
     * this verify we can put two same valid access value together 
     */
    @Test
    public void testInterfaceWithPublicAndPublicAccessCustomNamespace() throws Exception {
        String intfSource = "<aura:interface access='PUBLIC, PUBLIC'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_CUSTOM_NAMESPACE+":testInterface",
                        NamespaceAccess.CUSTOM);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    

    /*
     * These two verify we cannot use authentication as access, as we are not in InternalNamespace
     */
    @Test
    public void testInterfaceWithAuthenticationCustomNamespace() throws Exception {
        String intfSource = "<aura:interface access='AUTHENTICATED'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_CUSTOM_NAMESPACE+":testInterface",
                        NamespaceAccess.CUSTOM);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"AUTHENTICATED\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    @Test
    public void testInterfaceWithUnAuthenticationCustomNamespace() throws Exception {
        String intfSource = "<aura:interface access='UNAUTHENTICATED'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, "privilegedNS:testInterface",
                        NamespaceAccess.PRIVILEGED);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"UNAUTHENTICATED\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    
    /*
     * we cannot set access to Authenticated by method, like what we do for access value
     */
    @Test
    public void testInterfaceWithUnAuthenticationMethodCustomNamespace() throws Exception {
        String intfSource = "<aura:interface access='org.auraframework.impl.test.util.TestAccessMethods.allowAuthenticated'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_CUSTOM_NAMESPACE+":testInterface",
                        NamespaceAccess.CUSTOM);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "\"org.auraframework.impl.test.util.TestAccessMethods.allowAuthenticated\" must return a result of type org.auraframework.system.AuraContext$Access";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    /*
     * this verify we cannot have both AUTHENTICATED and UNAUTHENTICATED as access attribute
     */
    @Test
    public void testInterfaceWithAuthenticatedAndUnAuthenticationAccessCustomNamespace() throws Exception {
        String intfSource = "<aura:interface access='AUTHENTICATED,UNAUTHENTICATED'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_CUSTOM_NAMESPACE+":testInterface",
                        NamespaceAccess.CUSTOM);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Access attribute cannot specify both AUTHENTICATED and UNAUTHENTICATED";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    /*
     * This verify we cannot have Authentication as access attribute, as we are outside InternalNamespace
     */
    @Test
    public void testInterfaceWithAuthenticatedAndAuthenticationAccessCustomNamespace() throws Exception {
        String intfSource = "<aura:interface access='AUTHENTICATED,AUTHENTICATED'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_CUSTOM_NAMESPACE+":testInterface",
                        NamespaceAccess.CUSTOM);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"AUTHENTICATED\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    
    /*
     * These verify we cannot have authentication as part of access, as we are not in InternalNamespace. 
     */
    @Test
    public void testInterfaceWithAuthenticatedAndGlobalAccessCustomNamespace() throws Exception {
        String intfSource = "<aura:interface access='AUTHENTICATED,GLOBAL'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_CUSTOM_NAMESPACE+":testInterface",
                        NamespaceAccess.CUSTOM);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"AUTHENTICATED\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    @Test
    public void testInterfaceWithAuthenticatedAndPrivateAccessCustomNamespace() throws Exception {
        String intfSource = "<aura:interface access='AUTHENTICATED,PRIVATE'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_CUSTOM_NAMESPACE+":testInterface",
                        NamespaceAccess.CUSTOM);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"AUTHENTICATED\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    @Test
    public void testInterfaceWithAuthenticatedAndPublicAccessCustomNamespace() throws Exception {
        String intfSource = "<aura:interface access='AUTHENTICATED,PUBLIC'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_CUSTOM_NAMESPACE+":testInterface",
                        NamespaceAccess.CUSTOM);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"AUTHENTICATED\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    @Test
    public void testInterfaceWithAuthenticatedAndPrivilegedAccessCustomNamespace() throws Exception {
        String intfSource = "<aura:interface access='AUTHENTICATED,PRIVILEGED'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_CUSTOM_NAMESPACE+":testInterface",
                        NamespaceAccess.CUSTOM);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"AUTHENTICATED\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }
    @Test
    public void testInterfaceWithAuthenticatedAndInternalAccessCustomNamespace() throws Exception {
        String intfSource = "<aura:interface access='AUTHENTICATED,INTERNAL'/>";
        DefDescriptor<InterfaceDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(InterfaceDef.class,
                intfSource, StringSourceLoader.DEFAULT_CUSTOM_NAMESPACE+":testInterface",
                        NamespaceAccess.CUSTOM);
        TextSource<InterfaceDef> source = stringSourceLoader.getSource(descriptor);
        String expectedMsg = "Invalid access attribute value \"AUTHENTICATED\"";
        InvalidAccessValueException qfe = null;
        
        try {
            compilerService.compile(descriptor, source);
        } catch (InvalidAccessValueException e) {
            qfe = e;
        }
        assertNotNull("Expect to die with InvalidAccessValueException", qfe);
        assertTrue("Message '"+qfe.getMessage()+"' should contain '"+expectedMsg, qfe.getMessage().contains(expectedMsg));
    }

}
