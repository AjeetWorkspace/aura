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
import org.auraframework.def.TokensDef;
import org.auraframework.impl.AuraImplTestCase;
import org.auraframework.service.CompilerService;
import org.auraframework.system.TextSource;
import org.auraframework.test.source.StringSourceLoader;
import org.auraframework.test.source.StringSourceLoader.NamespaceAccess;
import org.auraframework.throwable.quickfix.InvalidAccessValueException;
import org.auraframework.util.test.annotation.UnAdaptableTest;
import org.junit.Test;


@UnAdaptableTest("when run in core, we throw error with different type.")
// Tokens not supported in custom, namespaces at this time
public class TokensAccessAttributeTest extends AuraImplTestCase {

    @Inject
    private CompilerService compilerService;

	    
    /***********************************************************************************
     ******************* Tests for Internal Namespace start ****************************
     ************************************************************************************/
    
    //testDefaultAccess
    @Test
    public void testTokensWithDefaultAccessInternalNamespace() throws Exception {
        String tokenSource = "<aura:tokens />";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, StringSourceLoader.DEFAULT_NAMESPACE+":testTokens",
                        NamespaceAccess.INTERNAL);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    
    //testEmptyAccess
    @Test
    public void testTokensWithEmptyAccessInternalNamespace() throws Exception {
        String tokenSource = "<aura:tokens access=''/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, StringSourceLoader.DEFAULT_NAMESPACE+":testTokens",
                        NamespaceAccess.INTERNAL);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithInvalidAccessInternalNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='BLAH'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, StringSourceLoader.DEFAULT_NAMESPACE+":testTokens",
                        NamespaceAccess.INTERNAL);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithInvalidAccessMethodInternalNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='org.auraframework.impl.test.util.TestAccessMethods.invalid'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, StringSourceLoader.DEFAULT_NAMESPACE+":testTokens",
                        NamespaceAccess.INTERNAL);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithInvalidAndValidAccessInternalNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='GLOBAL, BLAH, GLOBAL'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, StringSourceLoader.DEFAULT_NAMESPACE+":testTokens",
                        NamespaceAccess.INTERNAL);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithStaticAccessAndAccessMethodInternalNamespace() throws Exception {
            String tokenSource = "<aura:tokens access='GLOBAL,org.auraframework.impl.test.util.TestAccessMethods.allowGlobal' />";
            DefDescriptor<TokensDef> descriptor = 
                    getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                    tokenSource, StringSourceLoader.DEFAULT_NAMESPACE+":testTokens",
                        NamespaceAccess.INTERNAL);
            TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithAuthenticationAndAccessMethodInternalNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='org.auraframework.impl.test.util.TestAccessMethods.allowGlobal,AUTHENTICATED'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, StringSourceLoader.DEFAULT_NAMESPACE+":testTokens",
                        NamespaceAccess.INTERNAL);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
     * go through access=GLOBAL,PUBLIC,PRIVATE,INTERNAL,PRIVILEGED for tokens in InternalNamespace
     * notice: all are valid
     */
    @Test
    public void testTokensWithGlobalAccessInternalNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='GLOBAL'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, StringSourceLoader.DEFAULT_NAMESPACE+":testTokens",
                        NamespaceAccess.INTERNAL);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    @Test
    public void testTokensWithPublicAccessInternalNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='PUBLIC'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, StringSourceLoader.DEFAULT_NAMESPACE+":testTokens",
                        NamespaceAccess.INTERNAL);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    @Test
    public void testTokensWithPrivateAccessInternalNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='PRIVATE'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, StringSourceLoader.DEFAULT_NAMESPACE+":testTokens",
                        NamespaceAccess.INTERNAL);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    @Test
    public void testTokensWithInternalAccessInternalNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='INTERNAL'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, StringSourceLoader.DEFAULT_NAMESPACE+":testTokens",
                        NamespaceAccess.INTERNAL);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    @Test
    public void testTokensWithPrivilegedAccessInternalNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='PRIVILEGED'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, StringSourceLoader.DEFAULT_NAMESPACE+":testTokens",
                        NamespaceAccess.INTERNAL);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    
    /*
     * testSimpleAccessDynamicInSystemNamespace
     * go through access=GLOBAL,PUBLIC,PRIVATE,INTERNAL,PRIVILEGED by AccessMethod for tokens in InternalNamespace
     * notice that we only allow access method within internalNamespace.
     */
    @Test
    public void testTokensWithGlobalAccessMethodInternalNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='org.auraframework.impl.test.util.TestAccessMethods.allowGlobal'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, StringSourceLoader.DEFAULT_NAMESPACE+":testTokens",
                        NamespaceAccess.INTERNAL);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    @Test
    public void testTokensWithPublicAccessMethodInternalNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='org.auraframework.impl.test.util.TestAccessMethods.allowPublic'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, StringSourceLoader.DEFAULT_NAMESPACE+":testTokens",
                        NamespaceAccess.INTERNAL);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    //TODO: whey private is ok here?
    @Test
    public void testTokensWithPrivateAccessMethodInternalNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='org.auraframework.impl.test.util.TestAccessMethods.allowPrivate'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, StringSourceLoader.DEFAULT_NAMESPACE+":testTokens",
                        NamespaceAccess.INTERNAL);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    @Test
    public void testTokensWithPrivilegedAccessMethodInternalNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='org.auraframework.impl.test.util.TestAccessMethods.allowPrivileged'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, StringSourceLoader.DEFAULT_NAMESPACE+":testTokens",
                        NamespaceAccess.INTERNAL);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    @Test
    public void testTokensWithInternalAccessMethodInternalNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='org.auraframework.impl.test.util.TestAccessMethods.allowInternal'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, StringSourceLoader.DEFAULT_NAMESPACE+":testTokens",
                        NamespaceAccess.INTERNAL);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    
    
    /*
     * testCombinationAccessInSystemNamespace
     * this verify putting any two different valid access value together won't work. you can try other combinations, but it's the same
     */
    @Test
    public void testTokensWithGlobalAndPrivateAccessInternalNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='GLOBAL, PRIVATE'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, StringSourceLoader.DEFAULT_NAMESPACE+":testTokens",
                        NamespaceAccess.INTERNAL);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithPublicAndPublicAccessInternalNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='PUBLIC, PUBLIC'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, StringSourceLoader.DEFAULT_NAMESPACE+":testTokens",
                        NamespaceAccess.INTERNAL);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    

    /*
     * testSimpleAuthenticationInSystemNamespace
     */
    @Test
    public void testTokensWithAuthenticationInternalNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='AUTHENTICATED'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, StringSourceLoader.DEFAULT_NAMESPACE+":testTokens",
                        NamespaceAccess.INTERNAL);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithUnAuthenticationInternalNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='UNAUTHENTICATED'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, StringSourceLoader.DEFAULT_NAMESPACE+":testTokens",
                        NamespaceAccess.INTERNAL);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithUnAuthenticationMethodInternalNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='org.auraframework.impl.test.util.TestAccessMethods.allowAuthenticated'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, StringSourceLoader.DEFAULT_NAMESPACE+":testTokens",
                        NamespaceAccess.INTERNAL);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithAuthenticatedAndUnAuthenticationAccessInternalNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='AUTHENTICATED,UNAUTHENTICATED'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, StringSourceLoader.DEFAULT_NAMESPACE+":testTokens",
                        NamespaceAccess.INTERNAL);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithAuthenticatedAndAuthenticatedAccessInternalNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='AUTHENTICATED,AUTHENTICATED'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, StringSourceLoader.DEFAULT_NAMESPACE+":testTokens",
                        NamespaceAccess.INTERNAL);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
     * The only failing case is PRIVATE
     */
    @Test
    public void testTokensWithAuthenticatedAndGlobalAccessInternalNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='AUTHENTICATED,GLOBAL'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, StringSourceLoader.DEFAULT_NAMESPACE+":testTokens",
                        NamespaceAccess.INTERNAL);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithAuthenticatedAndPrivateAccessInternalNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='AUTHENTICATED,PRIVATE'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, StringSourceLoader.DEFAULT_NAMESPACE+":testTokens",
                        NamespaceAccess.INTERNAL);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithAuthenticatedAndPublicAccessInternalNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='AUTHENTICATED,PUBLIC'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, StringSourceLoader.DEFAULT_NAMESPACE+":testTokens",
                        NamespaceAccess.INTERNAL);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithAuthenticatedAndPrivilegedAccessInternalNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='AUTHENTICATED,PRIVILEGED'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, StringSourceLoader.DEFAULT_NAMESPACE+":testTokens",
                        NamespaceAccess.INTERNAL);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithAuthenticatedAndInternalAccessInternalNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='AUTHENTICATED,INTERNAL'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, StringSourceLoader.DEFAULT_NAMESPACE+":testTokens",
                        NamespaceAccess.INTERNAL);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithDefaultAccessPrivilegedNamespace() throws Exception {
        String tokenSource = "<aura:tokens/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, "privilegedNS:testTokens",
                        NamespaceAccess.PRIVILEGED);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    
    //testEmptyAccess
    @Test
    public void testTokensWithEmptyAccessPrivilegedNamespace() throws Exception {
        String tokenSource = "<aura:tokens access=''/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, "privilegedNS:testTokens",
                        NamespaceAccess.PRIVILEGED);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithInvalidAccessPrivilegedNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='BLAH'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, "privilegedNS:testTokens",
                        NamespaceAccess.PRIVILEGED);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithInvalidAccessMethodPrivilegedNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='org.auraframework.impl.test.util.TestAccessMethods.invalid'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, "privilegedNS:testTokens",
                        NamespaceAccess.PRIVILEGED);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithInvalidAndValidAccessPrivilegedNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='GLOBAL, BLAH, GLOBAL'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, "privilegedNS:testTokens",
                        NamespaceAccess.PRIVILEGED);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithStaticAccessAndAccessMethodPrivilegedNamespace() throws Exception {
            String tokenSource = "<aura:tokens access='GLOBAL,org.auraframework.impl.test.util.TestAccessMethods.allowGlobal' />";
            DefDescriptor<TokensDef> descriptor = 
                    getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                    tokenSource, "privilegedNS:testTokens",
                        NamespaceAccess.PRIVILEGED);
            TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithAuthenticationAndAccessMethodPrivilegedNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='org.auraframework.impl.test.util.TestAccessMethods.allowGlobal,AUTHENTICATED'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, "privilegedNS:testTokens",
                        NamespaceAccess.PRIVILEGED);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
     * go through access=GLOBAL,PUBLIC,PRIVATE,INTERNAL,PRIVILEGED for tokens in InternalNamespace
     * notice: only INTERNAL is invalid
     */
    @Test
    public void testTokensWithGlobalAccessPrivilegedNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='GLOBAL'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, "privilegedNS:testTokens",
                        NamespaceAccess.PRIVILEGED);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    @Test
    public void testTokensWithPublicAccessPrivilegedNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='PUBLIC'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, "privilegedNS:testTokens",
                        NamespaceAccess.PRIVILEGED);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    @Test
    public void testTokensWithPrivateAccessPrivilegedNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='PRIVATE'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, "privilegedNS:testTokens",
                        NamespaceAccess.PRIVILEGED);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    @Test
    public void testTokensWithInternalAccessPrivilegedNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='INTERNAL'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, "privilegedNS:testTokens",
                        NamespaceAccess.PRIVILEGED);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithPrivilegedAccessPrivilegedNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='PRIVILEGED'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, "privilegedNS:testTokens",
                        NamespaceAccess.PRIVILEGED);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    
    /*
     * testSimpleAccessDynamicInSystemNamespace
     * go through access=GLOBAL,PUBLIC,PRIVATE,INTERNAL,PRIVILEGED by AccessMethod for tokens in PrivilegedNamespace
     */
    @Test
    public void testTokensWithGlobalAccessMethodPrivilegedNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='org.auraframework.impl.test.util.TestAccessMethods.allowGlobal'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, "privilegedNS:testTokens",
                        NamespaceAccess.PRIVILEGED);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithPublicAccessMethodPrivilegedNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='org.auraframework.impl.test.util.TestAccessMethods.allowPublic'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, "privilegedNS:testTokens",
                        NamespaceAccess.PRIVILEGED);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithPrivateAccessMethodPrivilegedNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='org.auraframework.impl.test.util.TestAccessMethods.allowPrivate'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, "privilegedNS:testTokens",
                        NamespaceAccess.PRIVILEGED);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithPrivilegedAccessMethodPrivilegedNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='org.auraframework.impl.test.util.TestAccessMethods.allowPrivileged'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, "privilegedNS:testTokens",
                        NamespaceAccess.PRIVILEGED);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithInternalAccessMethodPrivilegedNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='org.auraframework.impl.test.util.TestAccessMethods.allowInternal'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, "privilegedNS:testTokens",
                        NamespaceAccess.PRIVILEGED);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithGlobalAndPrivateAccessPrivilegedNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='GLOBAL, PRIVATE'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, "privilegedNS:testTokens",
                        NamespaceAccess.PRIVILEGED);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithPublicAndPublicAccessPrivilegedNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='PUBLIC, PUBLIC'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, "privilegedNS:testTokens",
                        NamespaceAccess.PRIVILEGED);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
        
        compilerService.compile(descriptor, source);
    }
    

    /*
     * testSimpleAuthenticationInSystemNamespace, we cannot have authentication as access, as we are not in InternalNamespace
     */
    @Test
    public void testTokensWithAuthenticationPrivilegedNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='AUTHENTICATED'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, "privilegedNS:testTokens",
                        NamespaceAccess.PRIVILEGED);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithUnAuthenticationPrivilegedNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='UNAUTHENTICATED'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, "privilegedNS:testTokens",
                        NamespaceAccess.PRIVILEGED);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithUnAuthenticationMethodPrivilegedNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='org.auraframework.impl.test.util.TestAccessMethods.allowAuthenticated'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, "privilegedNS:testTokens",
                        NamespaceAccess.PRIVILEGED);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithAuthenticatedAndUnAuthenticationAccessPrivilegedNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='AUTHENTICATED,UNAUTHENTICATED'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, "privilegedNS:testTokens",
                        NamespaceAccess.PRIVILEGED);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithAuthenticatedAndAuthenticationAccessPrivilegedNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='AUTHENTICATED,AUTHENTICATED'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, "privilegedNS:testTokens",
                        NamespaceAccess.PRIVILEGED);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithAuthenticatedAndGlobalAccessPrivilegedNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='AUTHENTICATED,GLOBAL'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, "privilegedNS:testTokens",
                        NamespaceAccess.PRIVILEGED);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithAuthenticatedAndPrivateAccessPrivilegedNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='AUTHENTICATED,PRIVATE'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, "privilegedNS:testTokens",
                        NamespaceAccess.PRIVILEGED);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithAuthenticatedAndPublicAccessPrivilegedNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='AUTHENTICATED,PUBLIC'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, "privilegedNS:testTokens",
                        NamespaceAccess.PRIVILEGED);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithAuthenticatedAndPrivilegedAccessPrivilegedNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='AUTHENTICATED,PRIVILEGED'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, "privilegedNS:testTokens",
                        NamespaceAccess.PRIVILEGED);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
    public void testTokensWithAuthenticatedAndInternalAccessPrivilegedNamespace() throws Exception {
        String tokenSource = "<aura:tokens access='AUTHENTICATED,INTERNAL'/>";
        DefDescriptor<TokensDef> descriptor = 
                getAuraTestingUtil().addSourceAutoCleanup(TokensDef.class,
                tokenSource, "privilegedNS:testTokens",
                        NamespaceAccess.PRIVILEGED);
        TextSource<TokensDef> source = stringSourceLoader.getSource(descriptor);
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
