/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.power.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.cloudbus.cloudsim.power.models.PowerModelTest.assignHostForPowerModel;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author	Anton Beloglazov
 * @since	CloudSim Toolkit 2.0
 */
public class PowerModelSqrtTest {

    private static final double MAX_POWER = 200;
    private static final double STATIC_POWER_PERCENT = 0.3;

    private PowerModelSqrt powerModel;

    @BeforeEach
    public void setUp() {
        powerModel = assignHostForPowerModel(new PowerModelSqrt(MAX_POWER, STATIC_POWER_PERCENT));
    }

    @Test
    public void testGetMaxPower() {
        assertEquals(MAX_POWER, powerModel.getMaxPower());
    }

    @Test()
    public void testGetPowerArgumentLessThenZero() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> powerModel.getPower(-1));
    }

    @Test()
    public void testGetPowerArgumentLargerThenOne() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> powerModel.getPower(2));
    }

    @Test
    public void testGetPowerZeroUsage() {
        assertEquals(60, powerModel.getPower(0.0));
    }

    @Test
    public void testGetPowerHundredPercentUsage() {
        assertEquals(MAX_POWER, powerModel.getPower(1.0));
    }

    @Test
    public void testGetPowerCustomUsage() {
        final double expected = MAX_POWER * STATIC_POWER_PERCENT + (MAX_POWER - MAX_POWER * STATIC_POWER_PERCENT) / Math.sqrt(100) * Math.sqrt(0.5 * 100);
        assertEquals(expected, powerModel.getPower(0.5));
    }
}
