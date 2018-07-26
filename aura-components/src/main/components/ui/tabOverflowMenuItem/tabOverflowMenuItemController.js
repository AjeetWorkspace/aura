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
	/**
	 * Handler for event that's fired programatically
	 */
	focus: function(cmp) {
		cmp.find("overflowTrigger").setFocus();
	},
	
	tabSelected: function(cmp, evt, helper) {
		var focusedItemName = evt.getParam("selectedItem").get("v.id");
		helper.triggerTab(cmp, focusedItemName, null, false);
	},
	
	onMenuSelection: function(cmp, evt, helper) {
		var source = evt.getSource();
		helper.triggerTab(cmp, source.get("v.id"), null, true);
	},
	
	updateMenuItems: function(cmp, evt, helper) {
		helper.updateMenuItems(cmp);
	},

	handleDestroy: function(cmp, evt, helper) {
		helper.handleDestroy(cmp);
	}
	
})// eslint-disable-line semi