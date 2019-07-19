package com.test.vehicleParkingLot.service;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ParkingLotCommandFeedByFileTest {

	private ParkingLotCommandFeedByFile parkingLotCommandFeeder = new ParkingLotCommandFeedByFile("");

	private static final ByteArrayOutputStream bout = new ByteArrayOutputStream();
	private static final PrintStream ps = System.out;

	@Before
	public void setStandardOutputToByteArrayOutputStreem() {
		System.setOut(new PrintStream(bout));
	}

	@After
	public void setStandardOutputToItsPrevious() {
		System.setOut(new PrintStream(ps));
	}

	@Test
	public void testInvalidCommand() {
		// invalid park vehicle command
		parkingLotCommandFeeder.parseCommand("parking KA01HH1234 White");
		assertEquals("Invalid command", getActualOutput());
	}

	@Test
	public void testCorrectCommandButMissingArgumentsCommand() {

		// Test create parking lot with missing argument
		parkingLotCommandFeeder.parseCommand("create_parking_lot");
		assertEquals("Insufficient arguments passed to command", getActualOutput());

		// create vehicle parking lot with 1 slots
		parkingLotCommandFeeder.parseCommand("create_parking_lot 1");
		assertEquals(String.format("Created a parking lot with %d slots", 1), getActualOutput());

		// Test parking vehicle with missing argument
		parkingLotCommandFeeder.parseCommand("park");
		assertEquals("Insufficient arguments passed to command", getActualOutput());

		// Test leave vehicle with missing argument
		parkingLotCommandFeeder.parseCommand("leave");
		assertEquals("Insufficient arguments passed to command", getActualOutput());

		// Test get vehicle registration numbers by color with missing argument
		parkingLotCommandFeeder.parseCommand("registration_numbers_for_cars_with_colour");
		assertEquals("Insufficient arguments passed to command", getActualOutput());

		// Test get vehicle slots by color with missing argument
		parkingLotCommandFeeder.parseCommand("slot_numbers_for_cars_with_colour");
		assertEquals("Insufficient arguments passed to command", getActualOutput());

		// Test get vehicle slot by registration number with missing argument
		parkingLotCommandFeeder.parseCommand("slot_number_for_registration_number");
		assertEquals("Insufficient arguments passed to command", getActualOutput());

	}

	@Test
	public void testCreateParkingLot() {

		// create vehicle parking lot with 6 slots
		parkingLotCommandFeeder.parseCommand("create_parking_lot 6");
		assertEquals(String.format("Created a parking lot with %d slots", 6), getActualOutput());

		// create vehicle parking lot with 0 slots
		parkingLotCommandFeeder.parseCommand("create_parking_lot 0");
		assertEquals("Lot size should be positive integer", getActualOutput());

	}

	@Test
	public void testParkVehicleOnAllocatedSlot() {

		// Without vehicle parking lot creation try to park vehicle
		parkingLotCommandFeeder.parseCommand("park KA01HH1234 White");
		assertEquals("Lot not yet created", getActualOutput());

		// create vehicle parking lot with 2 slots
		parkingLotCommandFeeder.parseCommand("create_parking_lot 2");
		assertEquals(String.format("Created a parking lot with %d slots", 2), getActualOutput());

		// park vehicle
		parkingLotCommandFeeder.parseCommand("park KA01HH1234 White");
		assertEquals(String.format("Allocated slot number: %d", 1), getActualOutput());

		assertTrue(parkingLotCommandFeeder.getOccupiedSlots() == 1);
		assertTrue(parkingLotCommandFeeder.getNonOccupiedSlots() == 1);

		// park vehicle
		parkingLotCommandFeeder.parseCommand("park KA01HH9999 White");
		assertEquals(String.format("Allocated slot number: %d", 2), getActualOutput());

		assertTrue(parkingLotCommandFeeder.getOccupiedSlots() == 2);
		assertTrue(parkingLotCommandFeeder.getNonOccupiedSlots() == 0);

		// park vehicle
		parkingLotCommandFeeder.parseCommand("park KA01HH7777 Red");
		assertEquals("Sorry, parking lot is full", getActualOutput());

		// leave vehicle from slot 1
		parkingLotCommandFeeder.parseCommand("leave 1");
		assertEquals(String.format("Slot number: %d is free", 1), getActualOutput());

		// park vehicle
		parkingLotCommandFeeder.parseCommand("park KA01HH4321 green");
		assertEquals(String.format("Allocated slot number: %d", 1), getActualOutput());

		// park vehicle
		parkingLotCommandFeeder.parseCommand("park KA01HH7777 Red");
		assertEquals("Sorry, parking lot is full", getActualOutput());

	}

	@Test
	public void testLeaveVehicleFromAllocatedSlot() {

		// Without vehicle parking lot creation try to leave vehicle
		parkingLotCommandFeeder.parseCommand("leave 1");
		assertEquals("Lot not yet created", getActualOutput());

		// create vehicle parking lot with 2 slots
		parkingLotCommandFeeder.parseCommand("create_parking_lot 2");
		assertEquals(String.format("Created a parking lot with %d slots", 2), getActualOutput());

		// park vehicle
		parkingLotCommandFeeder.parseCommand("park KA01HH1234 White");
		assertEquals(String.format("Allocated slot number: %d", 1), getActualOutput());

		// park vehicle
		parkingLotCommandFeeder.parseCommand("park KA01HH9999 White");
		assertEquals(String.format("Allocated slot number: %d", 2), getActualOutput());

		// park vehicle
		parkingLotCommandFeeder.parseCommand("park KA01HH7777 Red");
		assertEquals("Sorry, parking lot is full", getActualOutput());

		// leave vehicle from slot 1
		parkingLotCommandFeeder.parseCommand("leave 1");
		assertEquals(String.format("Slot number: %d is free", 1), getActualOutput());

		// leave vehicle from slot 1
		parkingLotCommandFeeder.parseCommand("leave 1");
		assertEquals("Slot is empty", getActualOutput());

		assertTrue(parkingLotCommandFeeder.getOccupiedSlots() == 1);
		assertTrue(parkingLotCommandFeeder.getNonOccupiedSlots() == 1);

		// leave vehicle from slot 2
		parkingLotCommandFeeder.parseCommand("leave 2");
		assertEquals(String.format("Slot number: %d is free", 2), getActualOutput());

		assertTrue(parkingLotCommandFeeder.getOccupiedSlots() == 0);
		assertTrue(parkingLotCommandFeeder.getNonOccupiedSlots() == 2);

		// leave vehicle from slot 10 it is not yet exist
		parkingLotCommandFeeder.parseCommand("leave 10");
		assertEquals("Slot is not found", getActualOutput());

		// leave vehicle from slot 0 it is non positive integer should ask enter only
		// positive integer
		parkingLotCommandFeeder.parseCommand("leave 0");
		assertEquals("Slot number should be positive integer", getActualOutput());

		// leave vehicle from slot -40 it is non positive integer should ask enter only
		// positive integer
		parkingLotCommandFeeder.parseCommand("leave -40");
		assertEquals("Slot number should be positive integer", getActualOutput());

	}

	public String getActualOutput() {
		String actualOutput = bout.toString().trim();
		bout.reset();
		return actualOutput;
	}

}
