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
    initialize : function (cmp, event, helper) {
        helper.setDefaultAttrs(cmp);
        helper.handleNewValue(cmp);
    },
    handleCompositionStart : function(cmp) {
        cmp.set('v.privateIsCompositionstarted', true);
    },
    handleCompositionEnd : function(cmp) {
        cmp.set('v.privateIsCompositionstarted', false);
    },
    handleOnInput : function (cmp, event, helper) {
        if (cmp.get('v.privateIsCompositionstarted')) {
            // on iOS japanese keyboard, input gets refocused after finishing input
            // entry. This work around was put in to prevent duplicate value being
            // set. For example if enter 123 then value would be 123123 after refocus.
            cmp.set('v.inputValue', event.data);
        } else {
            cmp.set('v.inputValue', event.target.value);
        }

        if (helper.isInputValueValid(cmp)) {
            helper.updateLastInputValue(cmp);
            if (helper.weHaveToUpdate(cmp,'input')) {
                helper.setNewValue(cmp);
            }
        } else {
            helper.restoreLastInputValue(cmp);
        }
    },
    handleOnChange : function (cmp, event, helper) {
        // we stop propagation 'cause change event need to be fired only when
        // v.value get a new value
        event.stopPropagation();
        if (helper.weHaveToUpdate(cmp,'change')) {
            helper.setNewValue(cmp);
            helper.formatInputValue(cmp);
            helper.updateLastInputValue(cmp);
        }
    },
    handleOnBlur : function (cmp, event, helper) {
        if (helper.hasChangedValue(cmp)) {
            helper.setNewValue(cmp);
            helper.formatInputValue(cmp);
            helper.updateLastInputValue(cmp);
        } else {
            helper.formatInputValue(cmp);
        }
    },
    handleChangeEvent : function (cmp, event, helper) {
        if (helper.hasChangedValue(cmp)) {
            helper.handleNewValue(cmp);
        }
    },
    handleOnFocus : function (cmp, event, helper) {
        cmp.set('v.inputValue',helper.removeSymbols(cmp.get('v.inputValue')));
        helper.updateLastInputValue(cmp);
    }
});
