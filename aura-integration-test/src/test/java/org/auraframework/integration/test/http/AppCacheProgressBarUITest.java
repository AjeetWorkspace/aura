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
package org.auraframework.integration.test.http;

import org.auraframework.integration.test.util.WebDriverTestCase;
import org.auraframework.integration.test.util.WebDriverTestCase.TargetBrowsers;
import org.auraframework.system.AuraContext.Mode;
import org.auraframework.test.util.WebDriverUtil.BrowserType;
import org.auraframework.util.test.annotation.FreshBrowserInstance;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * UI automation for AppCache implementation.
 * 
 * ThreadHostile because simultaneous loads of the testApp will interfere with progress bar loading.
 * 
 * AppCache tests are only for webkit browsers. Excluded iOS for not supporting ProgressEvent.
 * 
 * TODO(W-1708575): Android AppCache tests fail when running on SauceLabs
 */
@TargetBrowsers({ BrowserType.GOOGLECHROME, BrowserType.SAFARI })
public class AppCacheProgressBarUITest extends WebDriverTestCase {
    private final String PROGRESSEVENTSCRIPT = "var evt = new ProgressEvent('%s', {%s});"
            + "window.applicationCache.dispatchEvent(evt);";
    private final String APPCACHEPROGRESS = String.format(PROGRESSEVENTSCRIPT, "progress", "loaded:%s, total:%s");
    private final String APPCACHENOUPDATE = String.format(PROGRESSEVENTSCRIPT, "noupdate", "");
    private final String APPCACHECACHED = String.format(PROGRESSEVENTSCRIPT, "cached", "");

    private final By appCacheProgressDiv = By.cssSelector("div[id='auraAppcacheProgress']");

    /**
     * Verify that progress bar shows true progress by simulating the progress event.
     */
    @Test
    public void testProgressBarBySimulatingProgressEvents() throws Exception {
        open("/appCache/testApp.app", Mode.DEV);
        getAuraUITestingUtil().waitForElementNotDisplayed(appCacheProgressDiv,
                "Progress bar for appCache is visible even after aura is ready.");

        // Step 1: Fire a progress event and verify that progress bar is visible
        getAuraUITestingUtil().getEval(String.format(APPCACHEPROGRESS, 1, 100));
        getAuraUITestingUtil().waitForElementDisplayed(appCacheProgressDiv,
                "Progress bar for appCache is not visible.");

        // Step 2: 50% progress
        getAuraUITestingUtil().getEval(String.format(APPCACHEPROGRESS, 50, 100));
        getAuraUITestingUtil().waitForElementDisplayed(appCacheProgressDiv,
                "Progress bar for appCache is not visible.");

        assertEquals("width: 50%;", findDomElement(By.cssSelector("div[class~='progressBar']")).getAttribute("style")
                .trim());

        // Step 3: Fire a cached event and verify that progress bar has
        // disappeared
        getAuraUITestingUtil().getEval(APPCACHECACHED);
        getAuraUITestingUtil().waitForElementNotDisplayed(appCacheProgressDiv,
                "Progress bar for appCache is visible even after 'cached' event is fired.");
    }

    /**
     * Verify that when a noupdate event is fired for appcache, the progress bar doesn't show up.
     */
    @Test
    public void testNoUpdateBySimulatingEvents() throws Exception {
        open("/appCache/testApp.app", Mode.DEV);

        // Step 1: Force the progress bar to show up
        getAuraUITestingUtil().getEval(String.format(APPCACHEPROGRESS, 1, 100));
        getAuraUITestingUtil().waitForElementDisplayed(appCacheProgressDiv,
                "Progress bar for appCache is not visible.");

        // Step 2: Fire noupdate event and make sure there is no progress bar
        getAuraUITestingUtil().getEval(APPCACHENOUPDATE);
        getAuraUITestingUtil().waitForElementNotDisplayed(appCacheProgressDiv,
                "Progress bar for appCache is visible even after 'noupdate' event is fired.");
    }

    /**
     * Verify that the progress bar doesn't show up in PROD mode.
     */
    @FreshBrowserInstance
    @Test
    public void testProgressbarNotVisibleInPRODMode() throws Exception {
        open("/appCache/testApp.app", Mode.PROD);

        // Timing issues make checking the progress bar on load flappy, so just simulate the event
        getAuraUITestingUtil().getEval(String.format(APPCACHEPROGRESS, 1, 100));
        assertFalse("Progress bar for appCache should not show up in PROD mode.", findDomElement(appCacheProgressDiv)
                .isDisplayed());
    }
}
