({
    // IndexedDB has problems in Safari and is not supported in older IE
    browsers:["-IE8", "-IE9", "-SAFARI", "-IPAD", "-IPHONE"],

    // Test modifies/deletes the persistent database
    labels : [ "threadHostile" ],

    /**
     * Verify getting new defs from server is not overly aggressive about evicting previously stored defs from storage.
     *
     * This covers the case where a bug in the pruning logic may cause defs to be evicted whenever any new def is
     * stored to the cache. This is tested with a component test instead of a unit test because the bug may only
     * be present in obfuscated javascript modes, as W-2833015 exemplifies.
     */
    testNoExcessivePruning: {
        test: [
            function clearPersistentStorages(cmp) {
                cmp._storageContents = cmp.helper.storageTestLib.storageContents;
                $A.test.addCleanup(function() {
                        $A.storageService.deleteStorage("ComponentDefStorage");
                    });

                return cmp.helper.clearActionAndDefStorage(cmp)
                    .catch(function(e) {
                        $A.test.fail("Error clearing actions or ComponentDefStorage stores: " + e);
                    });
            },
            function addFirstCmpToDefStorage(cmp) {
                var desc = "ui:tab";
                cmp.set("v.load", desc);
                cmp.fetchCmp();

                // def storage may not exist until the XHR is back and the def is stored
                $A.test.addWaitForWithFailureMessage(true, function() {
                        return !!$A.storageService.getStorage("ComponentDefStorage");
                    },
                    "ComponentDefStorage is not initialized.");
            },
            function waitForDefInStorage(cmp) {
                var desc = "ui:tab";
                return cmp._storageContents.waitForDefInStorage(desc);
            },
            function addToDefStorage(cmp) {
                var desc = "ui:tree"
                cmp.set("v.load", desc);
                cmp.fetchCmp();
                return cmp._storageContents.waitForDefInStorage(desc);
            },
            function addToDefStorage(cmp) {
                var desc = "ui:scroller";
                cmp.set("v.load", desc);
                cmp.fetchCmp();
                return cmp._storageContents.waitForDefInStorage(desc);
            },
            function verifyDefsNotEvicted(cmp) {
               // This test assumes pruning is done before inserting defs into the persistent def storage. By waiting
               // for each def to be put in persistent storage before continuing on, we know pruning has already
                // completed at this point so we can simply check what's currently in storage and verify no defs got
                // removed.

                // If defs are being pruned because the defs have gone over the storage maxSize we need to tweak the
                // size of ComponentDefStorage in the test template.
                var storage = $A.storageService.getStorage("ComponentDefStorage");
                return storage.getAll([], true)
                    .then(function(items) {
                        var defs = Object.keys(items);
                        var expectedDefs = ["markup://ui:scroller", "markup://ui:tab", "markup://ui:tree"];
                         expectedDefs.forEach(function(expected) {
                             $A.test.assertTrue(defs.indexOf(expected) > -1, expected + " not found in ComponentDefStorage");
                         });
                    });
            }
        ]
    }
})
