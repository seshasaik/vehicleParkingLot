package com.test.vehicleParkingLot.service;

public abstract class AbstractParkingLotCommandRunner {

	private class ParkingSlot {
		private int slotNumber;
		private Vehicle vehicle;

		public ParkingSlot(int slotNumber) {
			this.slotNumber = slotNumber;
		}

		public void addVehicle(Vehicle vehicle) {
			this.vehicle = vehicle;
		}

		public void removeVehicle() {
			this.vehicle = null;
		}

		public int getSlotNumber() {
			return slotNumber;
		}

		public Vehicle getVehicle() {
			return vehicle;
		}

	}

	private class Vehicle {

		private String registrationNumber;
		private String color;

		public Vehicle(String registrationNumber, String color) {
			this.registrationNumber = registrationNumber;
			this.color = color;
		}

		public String getRegistrationNumber() {
			return registrationNumber;
		}

		public String getColor() {
			return color;
		}

	}

	private String[] supportedCommands = { "create_parking_lot", "park", "leave", "status",
			"registration_numbers_for_cars_with_colour", "slot_numbers_for_cars_with_colour",
			"slot_number_for_registration_number"

	};

	public AbstractParkingLotCommandRunner() {
	}

	private ParkingSlot[] vehicleParkingLot = null;

	public abstract void commandFeeder();

	public void parseCommand(String rawCommand) {

		// remove any extra space on both ends
		rawCommand = rawCommand.trim();

		// If command is empty
		if (rawCommand.length() == 0) {
			return;
		}

		String[] commandParts = rawCommand.split(" ");

		// Validate command
		if (!isValidCommand(commandParts[0])) {
			System.out.println("\nInvalid command");
			return;
		}

		if (!commandParts[0].equals("create_parking_lot")) {

			if (!isLotCreated()) {
				System.out.println("\nLot not yet created");
				return;
			}

			if (getTotalLotSize() == 0) {
				System.out.println("\nLot is empty");
				return;
			}
		}

		invokeCommand(commandParts);

	}

	public void invokeCommand(String[] commandWithArgs) {

		try {
			switch (commandWithArgs[0]) {

			case "create_parking_lot":
				createParkingLot(Integer.parseInt(commandWithArgs[1]));
				break;
			case "park":
				putVehicleInAllocatedSlot(commandWithArgs[1], commandWithArgs[2]);
				break;
			case "leave":
				removeVehicleFromAllocatedSlot(Integer.parseInt(commandWithArgs[1]));
				break;
			case "status":
				showLotStatus();
				break;
			case "registration_numbers_for_cars_with_colour":
				// Query for Registration number by color
				vehicleParkingLotQuerys("color", commandWithArgs[1], "RegistrationNumber");
				break;
			case "slot_numbers_for_cars_with_colour":
				// Query for slot number by color
				vehicleParkingLotQuerys("color", commandWithArgs[1], "SlotNumber");
				break;
			case "slot_number_for_registration_number":
				// Query for slot number by registration number
				vehicleParkingLotQuerys("RegistrationNumber", commandWithArgs[1], "SlotNumber");
				break;

			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("\nInsufficient arguments passed to command");
			return;
		}

	}

	private boolean isValidCommand(String command) {

		for (int i = 0; i < supportedCommands.length; i++) {
			if (supportedCommands[i].equals(command)) {
				return true;
			}
		}
		return false;
	}

	private void createParkingLot(int lotSize) {

		if (lotSize <= 0) {
			System.out.println("\nLot size should be positive integer");
			return;
		}

		vehicleParkingLot = new ParkingSlot[lotSize];
		System.out.println(String.format("\nCreated a parking lot with %d slots", getTotalLotSize()));
	}

	private void putVehicleInAllocatedSlot(String registrationNumber, String color) {

		ParkingSlot parkingSlot = null;

		for (int i = 0; i < getTotalLotSize(); i++) {

			if (vehicleParkingLot[i] == null) {
				parkingSlot = new ParkingSlot(i + 1);
				vehicleParkingLot[i] = parkingSlot;
				break;
			}

			if (vehicleParkingLot[i] != null && vehicleParkingLot[i].getVehicle() == null) {
				parkingSlot = vehicleParkingLot[i];
				break;
			}
		}

		if (parkingSlot == null) {
			System.out.println("\nSorry, parking lot is full");
			return;
		}

		parkingSlot.addVehicle(new Vehicle(registrationNumber, color));

		System.out.println(String.format("\nAllocated slot number: %d", parkingSlot.getSlotNumber()));
	}

	private void removeVehicleFromAllocatedSlot(int slotNumber) {

		// Check if slotNumber is positive integer or not
		if (slotNumber <= 0) {
			System.out.println("\nSlot number should be positive integer");
			return;
		}

		boolean vehicleFound = false;
		boolean slotNumberFound = false;
		for (int i = 0; i < getTotalLotSize(); i++) {

			if (vehicleParkingLot[i] != null) {

				slotNumberFound = vehicleParkingLot[i].getSlotNumber() == slotNumber;

				if (slotNumberFound) {
					vehicleFound = vehicleParkingLot[i].getVehicle() != null;

					if (vehicleFound) {
						vehicleParkingLot[i].removeVehicle();
						System.out.println(
								String.format("\nSlot number: %d is free", vehicleParkingLot[i].getSlotNumber()));

					}

					break;

				}

			}

		}

		if (slotNumberFound && !vehicleFound) {
			System.out.println("\nSlot is empty");
			return;
		}

		if (!slotNumberFound) {
			System.out.println("\nSlot is not found");
			return;
		}
	}

	private void showLotStatus() {

		int occupiedSlots = 0;

		for (int i = 0; i < getTotalLotSize(); i++) {

			if (vehicleParkingLot[i] != null && vehicleParkingLot[i].getVehicle() != null) {

				if (++occupiedSlots == 1) {
					System.out.println("\nSlot No.\t Registration No \t Colour \t");
				}

				System.out.println(String.format("%d \t %s \t %s \t", vehicleParkingLot[i].getSlotNumber(),
						vehicleParkingLot[i].getVehicle().getRegistrationNumber(),
						vehicleParkingLot[i].getVehicle().getColor()));

			}

		}

		if (occupiedSlots == 0) {
			System.out.println("\nLot is empty");
		}

	}

	private void vehicleParkingLotQuerys(String queryParam, String value, String targetColumn) {
		String result = "";

		for (int i = 0; i < getTotalLotSize(); i++) {

			if (vehicleParkingLot[i] != null && vehicleParkingLot[i].getVehicle() != null) {

				if (targetColumn.equals("SlotNumber")) { // Slot number query

					if (queryParam.equals("color")
							&& vehicleParkingLot[i].getVehicle().getColor().equalsIgnoreCase(value)) {
						// based on car color
						result += vehicleParkingLot[i].getSlotNumber() + ", ";

					} else if (queryParam.equals("RegistrationNumber")
							&& vehicleParkingLot[i].getVehicle().getRegistrationNumber().equalsIgnoreCase(value)) {
						// based on registration_number
						result += vehicleParkingLot[i].getSlotNumber() + ", ";
						break;
					}

				} else if (targetColumn.equals("RegistrationNumber")) { // registration numbers query
					if (queryParam.equals("color")
							&& vehicleParkingLot[i].getVehicle().getColor().equalsIgnoreCase(value)) {
						// based on car color
						result += vehicleParkingLot[i].getVehicle().getRegistrationNumber() + ", ";
					}
				}

			}

		}

		result = result.trim();

		if (result.length() == 0) {
			System.out.println("\nNot found");
			return;
		}

		System.out.println("\n"+result.substring(0, result.length() - 1));

	}

	protected boolean isLotCreated() {
		return vehicleParkingLot != null;
	}

	protected int getTotalLotSize() {

		return vehicleParkingLot.length;
	}

	protected int getOccupiedSlots() {
		int occupiedSlots = 0;
		for (int i = 0; i < getTotalLotSize(); i++) {

			if (vehicleParkingLot[i] != null && vehicleParkingLot[i].getVehicle() != null) {
				++occupiedSlots;
			}

		}
		return occupiedSlots;

	}

	protected int getNonOccupiedSlots() {
		int nonOccupiedSlots = 0;
		for (int i = 0; i < getTotalLotSize(); i++) {

			if (vehicleParkingLot[i] == null || vehicleParkingLot[i].getVehicle() == null) {
				++nonOccupiedSlots;
			}

		}
		return nonOccupiedSlots;
	}

}
