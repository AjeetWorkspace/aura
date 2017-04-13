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
({
    testLabel: {
        attributes : {label : 'shucks'},
        test: function(component){
            $A.test.assertEquals('shucks', $A.test.getText(component.find("span").getElement()), "Label not found");
        }
    },
    testHTMLElementType:{
        attributes : {label : 'Ok'},
        test:function(component){
            var element = component.getElement();
            $A.test.assertNotNull(element, "Unable to retrieve Dom element information for ui button");
            $A.test.assertTrue($A.test.isInstanceOfButtonElement(element), "ui:button should be rendering a button element.");
        }
    },
    testDefaultPropertiesOfUiButton:{
        attributes : {label : 'Ok'},
        test:function(component){
            var element = component.getElement();
            $A.test.assertEquals('button', component.get('v.buttonType'), "default value of buttonType attribute should be 'button'");
            $A.test.assert('button',element.getAttribute('type'), "By default ui:button should create a button element of type 'Button'");

            $A.test.assertTrue(!component.get('v.buttonTitle'), "Button should not have a default value for title");

            $A.test.assertFalse($A.util.getBooleanValue(component.get('v.disabled')), "Button should not be disabled by default");
            $A.test.assertFalse(element.disabled, "By default dom element for ui:button should not be disabled");

            $A.test.assertEquals('ltr', component.get('v.labelDir'), "Button label should be left to right by default");

            $A.test.assertUndefined(component.get('v.accesskey'), "Button should have no shortcut key by default");
            $A.test.assertEquals("", element.accessKey, "By default dom element for ui:button should have no shortcut key");
        }
    },
    testTitle:{
        attributes:{label : 'Ok', buttonTitle:'Button Title'},
        test:function(component)    {
        	$A.test.assertEquals('Button Title',component.find("button").getElement().title, "Rendered button does not have a title");
        }
    },
    testButtonType:{
        attributes:{label : 'Ok', buttonType: 'reset'},
        test:function(component){
        	$A.test.assertEquals('reset', component.find("button").getElement().getAttribute("type"), "Button not rendered with specified type");
        }
    },
    
    testInvalidButtonType:{
        attributes:{buttonType: 'fooBar'},
        test:function(component){
        	$A.test.assertEquals('submit', component.find("button").getElement().type, "Button not rendered with default type when a bad type is specified");
        }
    },
    
    testLabelClass:{
        attributes:{label:'Ok', labelClass: 'OkStyling'},
        test:function(component){
        	$A.test.assertNotEquals(component.find("span").getElement().className.toString().indexOf('OkStyling'), -1,
                    "Button not rendered with specified labelStyle class");
        }
    },
    
    testButtonWithIcon:{
        attributes:{label : 'Ok', iconImgSrc:'/auraFW/resources/aura/images/bug.png', iconClass:'Red'},
        test:function(component){
            for(var i in component.find('button').getElement().children){
                var child = component.find('button').getElement().children[i];
                if($A.test.isInstanceOfImageElement(child)){
                	$A.test.assertNotEquals(child.className.toString().indexOf('Red'), -1 ,
                            "Button not rendered with specified iconClass");
                	$A.test.assertNotEquals(child.src.indexOf('/auraFW/resources/aura/images/bug.png'), -1,
                            "Button not rendered with specified icon img");
                    return;
                }
            }
            $A.test.fail("Could not attach an image icon to button");
        }
    },

    testButtonWithoutIcon:{
        attributes:{},
        test:function(component){
            for(var i in component.find('button').getElement().children){
                var child = component.find('button').getElement().children[i];
                if($A.test.isInstanceOfImageElement(child)){
                    $A.test.fail("There should be no image element for this button");
                }
            }
        }
    },
    testDisabled:{
        attributes:{label : 'Ok', disabled:true},
           test:function(component){
        	   $A.test.assertTrue(component.find("button").getElement().disabled, "Button was not rendered in disabled state");
        }
    },
    testAccesskey:{
        attributes:{label : 'Ok', accesskey:'A'},
        test:function(component){
        	$A.test.assertEquals('A',component.find("button").getElement().accessKey, "Button not rendered with the specified accesskey");
        }
    },
    testLabelDir:{
        attributes:{label:"Click right Now!", labelDir:'rtl'},
        test:function(component){
        	$A.test.assertEquals('rtl', component.find('span').getElement().dir, "Label not rendered with specified text direction.");
        }
    },
    testLabelNotRequired:{
        attributes:{iconImgSrc:'/auraFW/resources/aura/images/bug.png'},
        test:function(component){
            var button = component.find('button');
            var img = button.getElement().children[0];
            $A.test.assertTrue($A.test.isInstanceOfImageElement(img), "Button not rendered with img tag");
            var span = component.find("span");
            $A.test.assertUndefinedOrNull(span, "No visible label span should appear when label is null");
            var hidden = component.find("hidden");
            $A.test.assertUndefinedOrNull(hidden, "No hidden label span should appear when label is null");
        }
    },
    
    /**
     * Verify aria-live is set correctly
     * W-3338253
     */
    testAriaLiveSetAssertive: {
        attributes : {stateful : true},
        test: function(component){
        	$A.test.assertEquals('assertive', component.find("button").getElement().getAttribute("aria-live"), "Button not rendered with aria-live='assertive'");
        }
    },
    
    testAriaLiveNotSet: {
        test: function(component){
        	$A.test.assertEquals('off', component.find("button").getElement().getAttribute("aria-live"), "Button not rendered with aria-live='off'");
        }
    },
    
    testRerender:{
        attributes:{label:"Like", disabled:false, hasIcon:true, iconImgSrc:'/auraFW/resources/aura/images/bug.png'},
        test:function(component){
        	$A.test.assertEquals('Like', $A.test.getText(component.find("span").getElement()), "Label not correct");
        	$A.test.assertFalse(component.find("button").getElement().disabled, "Button was rendered in disabled state");

            component.set('v.disabled', true);
            component.set('v.label', 'clear');
            component.set('v.iconImgSrc', '/auraFW/resources/aura/images/clear.png');
            

            $A.renderingService.rerenderDirty();

            $A.test.assertEquals('clear', $A.test.getText(component.find("span").getElement()), "New label not rerendered");
            $A.test.assertTrue(component.find("button").getElement().disabled, "Button was not rerendered in disabled state");

            var i, child;
            for(i in component.find('button').getElement().children){
                child = component.find('button').getElement().children[i];
                if($A.test.isInstanceOfImageElement(child)){
                	$A.test.assertTrue(child.src.indexOf('/auraFW/resources/aura/images/clear.png')!==-1, "Button not rerendered with specified icon img");
                }
            }
            
        }
    }

/*eslint-disable semi*/
})
/*eslint-enable semi*/
