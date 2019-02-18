/*
 *  This file contains Good Sample Code subject to the Good Dynamics SDK Terms and Conditions.
 *  (c) 2016 Good Technology Corporation. All rights reserved.
 */
package com.good.gd.example.skeleton;

import com.good.automated.general.utils.AbstractUIAutomatorUtils;
import com.good.automated.general.utils.UIAutomatorUtilsFactory;

import android.support.test.runner.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertTrue;

/**
 * Tests purpose - Ensure SecureSQL sample app correct basic operation
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class test_suite1 {

    /*
    Note - The test order of these tests is significant hence the explict test numbering
     */

    private static AbstractUIAutomatorUtils uiAutomatorUtils = UIAutomatorUtilsFactory.getUIAutomatorUtils();

    /**
     * Setup Test, like all tests makes use of helper functions in GD_UIAutomator_Lib Test library project
     */
    @BeforeClass
    public static void setUpClass() {

        uiAutomatorUtils.wakeUpDeviceIfNeeded();

        //Android Emulator when booted sometimes has error dialogues to dismiss
        uiAutomatorUtils.acceptSystemDialogues();

    }

    @Test
    public void test_1_activation() throws Exception {

        uiAutomatorUtils.launchAppUnderTest();

        assertTrue("Password entering failed", uiAutomatorUtils.enterTextToItemWithText("Enter Password", "q", 1));
        assertTrue("Go button pressing failed", uiAutomatorUtils.clickOnItemWithText("GO", 1));

        assertTrue("Entering user from list failed", uiAutomatorUtils.clickOnItemWithText("User_1", 3));

        assertTrue("Sending message failed", uiAutomatorUtils.clickOnItemWithText("SEND", 1));
        assertTrue("Receiving message failed", uiAutomatorUtils.isResourceWithIDShown("com.good.mygoodsample", "received_message", 15));

        uiAutomatorUtils.pressHome();

    }

}