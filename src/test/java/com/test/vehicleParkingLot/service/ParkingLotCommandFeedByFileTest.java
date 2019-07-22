package com.test.vehicleParkingLot.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
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

		// park vehicle which is already exist in given slot
		parkingLotCommandFeeder.parseCommand("park KA01HH1234 White");
		assertEquals(String.format("Sorry, vehicle is alreday found in %d slot", 1), getActualOutput());

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

	@Test
	public void testRegistrationNumbersForCarsWithColour() {

		// Without vehicle parking lot creation try to query for registration number by
		// given color
		parkingLotCommandFeeder.parseCommand("registration_numbers_for_cars_with_colour White");
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

		// query for counting vehicle registration number by given color(white)
		parkingLotCommandFeeder.parseCommand("registration_numbers_for_cars_with_colour White");
		assertTrue("Present there are vehicles with white color is 2", 2 == getActualOutput().split(",").length);

		// query for vehicle registration number by given color(white)
		parkingLotCommandFeeder.parseCommand("registration_numbers_for_cars_with_colour White");
		assertEquals("KA01HH1234, KA01HH9999", getActualOutput());

		// query for counting vehicle registration number by given color(red)
		parkingLotCommandFeeder.parseCommand("registration_numbers_for_cars_with_colour red");
		assertTrue("Present there are vehicles with white color is 0", "Not found".equals(getActualOutput()));

		// leave vehicle from slot 1
		parkingLotCommandFeeder.parseCommand("leave 1");
		assertEquals(String.format("Slot number: %d is free", 1), getActualOutput());

		// query for vehicle registration number by given color(white) after leave one
		// vehicle form slot
		parkingLotCommandFeeder.parseCommand("registration_numbers_for_cars_with_colour White");
		assertEquals("KA01HH9999", getActualOutput());

		assertTrue(parkingLotCommandFeeder.getOccupiedSlots() == 1);
		assertTrue(parkingLotCommandFeeder.getNonOccupiedSlots() == 1);

	}

	@Test
	public void testSlotNumbersForCarsWithColour() {

		// Without vehicle parking lot creation try to query for slot numbers by
		// given color
		parkingLotCommandFeeder.parseCommand("slot_numbers_for_cars_with_colour White");
		assertEquals("Lot not yet created", getActualOutput());

		// create vehicle parking lot with 3 slots
		parkingLotCommandFeeder.parseCommand("create_parking_lot 3");
		assertEquals(String.format("Created a parking lot with %d slots", 3), getActualOutput());

		// park vehicle
		parkingLotCommandFeeder.parseCommand("park KA01HH1234 White");
		assertEquals(String.format("Allocated slot number: %d", 1), getActualOutput());

		// park vehicle
		parkingLotCommandFeeder.parseCommand("park KA01HH7777 Red");
		assertEquals(String.format("Allocated slot number: %d", 2), getActualOutput());

		// park vehicle
		parkingLotCommandFeeder.parseCommand("park KA01HH9999 White");
		assertEquals(String.format("Allocated slot number: %d", 3), getActualOutput());

		// query for counting slot numbers by given color(white)
		parkingLotCommandFeeder.parseCommand("slot_numbers_for_cars_with_colour White");
		assertTrue("Present there are vehicles with white color is 2", 2 == getActualOutput().split(",").length);

		// query for slot number by given color(white)
		parkingLotCommandFeeder.parseCommand("slot_numbers_for_cars_with_colour White");
		assertEquals("1, 3", getActualOutput());

		// query for counting slot number by given color(red)
		parkingLotCommandFeeder.parseCommand("slot_numbers_for_cars_with_colour red");
		assertTrue("Present there are vehicles with white red is 1", 1 == getActualOutput().split(",").length);

		// query for vehicle slot number by given color(black)
		parkingLotCommandFeeder.parseCommand("slot_numbers_for_cars_with_colour red");
		assertEquals("2", getActualOutput());

		// query for counting slot number by given color(black)
		parkingLotCommandFeeder.parseCommand("slot_numbers_for_cars_with_colour black");
		assertTrue("Present there are vehicles with white red is 0", "Not found".equals(getActualOutput()));

		// leave vehicle from slot 1
		parkingLotCommandFeeder.parseCommand("leave 1");
		assertEquals(String.format("Slot number: %d is free", 1), getActualOutput());

		// query for counting slot numbers by given color(white)
		parkingLotCommandFeeder.parseCommand("slot_numbers_for_cars_with_colour White");
		assertTrue("Present there are vehicles with white color is 1", 1 == getActualOutput().split(",").length);

		// query for slot number by given color(white)
		parkingLotCommandFeeder.parseCommand("slot_numbers_for_cars_with_colour White");
		assertEquals("3", getActualOutput());

		assertTrue(parkingLotCommandFeeder.getOccupiedSlots() == 2);
		assertTrue(parkingLotCommandFeeder.getNonOccupiedSlots() == 1);

	}

	@Test
	public void testSlotNumbersForCarsWithRegistrationNumber() {

		// Without vehicle parking lot creation try to query for slot numbers by
		// given registration number
		parkingLotCommandFeeder.parseCommand("slot_number_for_registration_number KA01HH9999");
		assertEquals("Lot not yet created", getActualOutput());

		// create vehicle parking lot with 3 slots
		parkingLotCommandFeeder.parseCommand("create_parking_lot 3");
		assertEquals(String.format("Created a parking lot with %d slots", 3), getActualOutput());

		// park vehicle
		parkingLotCommandFeeder.parseCommand("park KA01HH1234 White");
		assertEquals(String.format("Allocated slot number: %d", 1), getActualOutput());

		// park vehicle
		parkingLotCommandFeeder.parseCommand("park KA01HH7777 Red");
		assertEquals(String.format("Allocated slot number: %d", 2), getActualOutput());

		// park vehicle
		parkingLotCommandFeeder.parseCommand("park KA01HH9999 White");
		assertEquals(String.format("Allocated slot number: %d", 3), getActualOutput());

		// query for counting slot numbers by given registration number (which is
		// already exist in lot)
		parkingLotCommandFeeder.parseCommand("slot_number_for_registration_number KA01HH9999");
		assertTrue("Present there are vehicles with registration number KA01HH9999 is 1",
				1 == getActualOutput().split("/").length);

		// query for slot number by given registration number (which is already exist in
		// lot)
		parkingLotCommandFeeder.parseCommand("slot_number_for_registration_number KA01HH9999");
		assertEquals("3", getActualOutput());

		// query for counting slot numbers by given registration number (which is
		// already exist in lot)
		parkingLotCommandFeeder.parseCommand("slot_number_for_registration_number KA01HH7777");
		assertTrue("Present there are vehicles with registration number KA01HH7777 is 1",
				1 == getActualOutput().split("/").length);

		// query for slot number by given registration number (which is already exist in
		// lot)
		parkingLotCommandFeeder.parseCommand("slot_number_for_registration_number KA01HH7777");
		assertEquals("2", getActualOutput());

		// query for counting slot numbers by given registration number (which is not
		// exist in lot)
		parkingLotCommandFeeder.parseCommand("slot_number_for_registration_number AP13HH7777");
		assertTrue("Present there are vehicles with registration number AP13HH7777 is 0",
				"Not found".equals(getActualOutput()));

		// leave vehicle with registration number KA01HH9999 from slot 3 (which is
		// already exist in lot but removed form lot)
		parkingLotCommandFeeder.parseCommand("leave 3");
		assertEquals(String.format("Slot number: %d is free", 3), getActualOutput());

		// query for counting slot numbers by given registration number (which is
		// previously exist in lot but removed form lot)
		parkingLotCommandFeeder.parseCommand("slot_number_for_registration_number KA01HH9999");
		assertTrue("Present there are vehicles with registration number KA01HH9999 is 0",
				"Not found".equals(getActualOutput()));

		assertTrue(parkingLotCommandFeeder.getOccupiedSlots() == 2);
		assertTrue(parkingLotCommandFeeder.getNonOccupiedSlots() == 1);

	}

	public String getActualOutput() {
		String actualOutput = bout.toString().trim();
		bout.reset();
		return actualOutput;
	}

}
