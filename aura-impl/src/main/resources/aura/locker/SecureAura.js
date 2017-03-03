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

/**
 * Factory for SecureAura objects.
 *
 * @param {Object}
 *            AuraInstance - the Aura Instance to be secured
 * @param {Object}
 *            key - the key to apply to the secure aura
 */
function SecureAura(AuraInstance, key) {
    "use strict";

    var o = ls_getFromCache(AuraInstance, key);
    if (o) {
        return o;
    }

    var su = Object.create(null);
    var sls = Object.create(null);
    o = Object.create(null, {
        "util" : {
            writable : true,
            enumerable : true,
            value : su
        },
        "localizationService" : {
            writable : true,
            enumerable : true,
            value : sls
        },
        "getCallback" : {
            value : function(f) {
                // If the results of $A.getCallback() is wired up to an event handler, passed as an attribute or aura event attribute etc it will get
                // filtered and wrapped with the caller's perspective at that time.
                return AuraInstance.getCallback(f);
            }
        },
        toString : {
            value : function() {
                return "SecureAura: " + AuraInstance + "{ key: " + JSON.stringify(key) + " }";
            }
        }
    });

    // SecureAura methods and properties
    [ "createComponent", "createComponents", "enqueueAction" ].forEach(function(name) {
        Object.defineProperty(o, name, SecureObject.createFilteredMethod(o, AuraInstance, name, { rawArguments: true }));
    });

    [ "reportError", "get", "getComponent", "getReference", "getRoot", "log", "warning" ].forEach(function(name) {
        Object.defineProperty(o, name, SecureObject.createFilteredMethod(o, AuraInstance, name));
    });

    ls_setRef(o, AuraInstance, key);
    Object.seal(o);

    // SecureUtil: creating a proxy for $A.util
    [ "addClass", "getBooleanValue", "hasClass", "isArray", "isEmpty", "isObject", "isUndefined", "isUndefinedOrNull", "removeClass", "toggleClass" ].forEach(function(name) {
        Object.defineProperty(su, name, SecureObject.createFilteredMethod(su, AuraInstance["util"], name));
    });

    ls_setRef(su, AuraInstance["util"], key);
    Object.seal(su);

    // SecureLocalizationService: creating a proxy for $A.localizationService
    [ "displayDuration", "displayDurationInDays", "displayDurationInHours", "displayDurationInMilliseconds", "displayDurationInMinutes",
        "displayDurationInMonths", "displayDurationInSeconds", "duration", "endOf", "formatCurrency", "formatDate", "formatDateTime", "formatDateTimeUTC",
        "formatDateUTC", "formatNumber", "formatPercent", "formatTime", "formatTimeUTC", "getDateStringBasedOnTimezone", "getDaysInDuration",
        "getDefaultCurrencyFormat", "getDefaultNumberFormat", "getDefaultPercentFormat", "getHoursInDuration", "getLocalizedDateTimeLabels",
        "getMillisecondsInDuration", "getMinutesInDuration", "getMonthsInDuration", "getNumberFormat", "getSecondsInDuration", "getToday",
        "getYearsInDuration", "isAfter", "isBefore", "isBetween", "isPeriodTimeView", "isSame", "parseDateTime", "parseDateTimeISO8601", "parseDateTimeUTC", "startOf",
        "toISOString", "translateFromLocalizedDigits", "translateFromOtherCalendar", "translateToLocalizedDigits", "translateToOtherCalendar",
        "UTCToWallTime", "WallTimeToUTC" ].forEach(function(name) {
            Object.defineProperty(sls, name, SecureObject.createFilteredMethod(sls, AuraInstance["localizationService"], name));
        });

    ls_setRef(sls, AuraInstance["localizationService"], key);
    Object.seal(sls);

    ls_addToCache(AuraInstance, o, key);
    ls_registerProxy(o);

    return o;
}
