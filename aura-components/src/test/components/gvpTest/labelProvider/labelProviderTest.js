({
    testAsyncLabelProvider: {
        test: function (cmp) {

            $A.test.addWaitFor(
                false,
                $A.test.isActionPending,
                function () {
                    $A.test.assertEquals("Today", cmp.get("v.simplevalue1"), "Failed to get Label");
                }
            );
        }
    },

    testNonExistingSection: {
        test: function (cmp) {
            $A.test.addWaitFor(
                false,
                $A.test.isActionPending,
                function () {
                    var label = "$Label.DOESNT.EXIST";
                    var sv2 = cmp.get("v.simplevalue2");

                    $A.test.assertTrue(
                        sv2 === null ||
                        sv2.indexOf(label + " does not exist") !== -1,
                        "Failed to get expected error message: " + sv2);
                }
            );
        }
    },

    testNonExistingLabel: {
        test: function (cmp) {
            $A.test.addWaitFor(
                false,
                $A.test.isActionPending,
                function () {
                    var label = "$Label.Related_Lists.DOESNTEXIST";
                    var sv3 = cmp.get("v.simplevalue3");

                    $A.test.assertTrue(
                        sv3 === null ||
                        sv3.indexOf(label + " does not exist") !== -1,
                        "Failed to get expected error message: " + sv3);
                }
            );

        }
    },

    testPartialLabelExpressions: {
        test: [
            //Wait for all labels to be fetched and GVP to be ready
            function (cmp) {
                $A.test.addWaitFor(
                    false,
                    $A.test.isActionPending
                );
            },

// JBUCH: HALO: FIXME: THIS PATTERN IS CURRENTLY ALLOWED. MORE DISCUSSION NECESSARY.
            //Section and name missing from label expression
//            function (cmp) {
//                var labels = $A.get("$Label");
//                $A.test.assertUndefinedOrNull(labels, "$Label should be undefined");
//            },
            //Expression without Name missing but valid section
            function (cmp) {
                var section = $A.get("$Label.Related_Lists");
                $A.test.assertUndefinedOrNull(section, "$Label.Related_Lists should be undefined");
            },
            //Expression without an invalid section only
            function (cmp) {
                $A.test.addWaitFor(
                    false,
                    $A.test.isActionPending,
                    function () {
                        var sv4 = cmp.get("v.simplevalue4");
                        $A.test.assertUndefinedOrNull(sv4, "v.simplevalue4 should be undefined");
                    }
                );
            }
        ]
    },

    /**
     * General tests for Global Value Providers
     */
    testGetWithCallback: {
        test: [
            //Fetch a new label from server
            function (cmp) {
                var completed = false;
                var actual;

                var label = "$Label.Related_Lists.FooBar";
                $A.get(label, function (value) {
                        completed = true;
                        actual = value;
                    });

                $A.test.addWaitForWithFailureMessage(true, function () {
                        return completed;
                    },
                    "Failed to run call back after fetching label from server",
                    function () {
                        $A.test.assertTrue(
                            actual === null ||
                            actual.indexOf(label + " does not exist") > -1,
                            "$Label.Related_Lists.FooBar should have error value: " + actual
                        );
                    });
            },
            //Fetch existing GVPs at client
            function (cmp) {
                var actual;

                var label = "$Label.Related_Lists.FooBar";
                $A.get(label, function (value) {
                        actual = value;
                    });

                //No need to wait for unlike previous case, call backs are immediate as value is available at client
                $A.test.assertTrue(
                    actual === null ||
                    actual.indexOf(label + " does not exist") > -1,
                    "$Label.Related_Lists.FooBar should have error value: " + actual
                );
            }
        ]
    },

    testGetWithoutCallbackUpatesLabel: {
        test : function (cmp) {
            // The label does not exist on client and will request it from server
            $A.get("$Label.Related_Lists.task_mode_today_overdue");

            $A.test.addWaitFor("Today + Overdue", function() {
                return $A.get("$Label.Related_Lists.task_mode_today_overdue");
            });
        }
    },

    /**
     * Verify that $Label is not processed by JSON.resolveRefs.
     * Labels retrieved via $A.get go via a different path, so we need to access the provider via an expression.
     */
    testLabelJsonIsNotResolved: {
        test: [
            function (cmp) {
                $A.test.assertEquals("serialId", $A.test.getText(cmp.find("json_s").getElement()),
                    "'s' should not have been resolved within $Label");
                $A.test.assertEquals("serialIdShort", $A.test.getText(cmp.find("json_sid").getElement()),
                    "'sid' should not have been resolved within $Label");
                $A.test.assertEquals("serialRefId", $A.test.getText(cmp.find("json_r").getElement()),
                    "'r' should not have been resolved within $Label");
                $A.test.assertEquals("serialRefIdShort", $A.test.getText(cmp.find("json_rid").getElement()),
                    "'rid' should not have been resolved within $Label");
            }
        ]
    }
})
