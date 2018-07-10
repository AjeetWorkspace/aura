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
    render: function outputNumberRender(cmp) {
        var span = this.superRender()[0];
        var f = cmp.get("v.format");
        var num = cmp.get("v.value");
        var formatted;
        if (($A.util.isNumber(num) || $A.util.isString(num)) && !$A.util.isEmpty(num)) {    		
            if (!$A.util.isEmpty(f)) {
                var nf;
                try {
                    nf = $A.localizationService.getNumberFormat(f);
                } catch (e) {
                    formatted = "Invalid format attribute";
                    $A.log(e);
                }
                if (nf) {
                    try {
                        formatted = nf.format(num);
                    } catch (e) {
                        formatted = "Invalid number value";
                        $A.log(e);
                    }
                }
            } else {
                try {
                    formatted = $A.localizationService.formatNumber(num);
                } catch (e) {
                    formatted = "Invalid number value";
                    $A.log(e);
                }
            }
            span.textContent = span.innerText = formatted;
        }
        return span;
    },

    rerender: function outputNumberRerenderer(cmp) {
        var val = cmp.get("v.value");
        var f = cmp.get("v.format");
        var formatted = '';
        if (($A.util.isNumber(val) || $A.util.isString(val)) && !$A.util.isEmpty(val)) {
            if (!$A.util.isEmpty(f)) {
                var nf;
                try {
                    nf = $A.localizationService.getNumberFormat(f);
                } catch (e) {
                    formatted = "Invalid format attribute";
                    $A.log(e);
                }
                if (nf) {
                    try {
                        formatted = nf.format(val);
                    } catch (e) {
                        formatted = "Invalid number value";
                        $A.log(e);
                    }
                }
            } else {
                try {
                    formatted = $A.localizationService.formatNumber(val);
                } catch (e) {
                    formatted = "Invalid number value";
                    $A.log(e);
                }
            }
        }
        var span = cmp.find("span");
        span.getElement().textContent = span.getElement().innerText = formatted;
    }
 })// eslint-disable-line semi
