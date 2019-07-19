package com.test.vehicleParkingLot;

import com.test.vehicleParkingLot.service.AbstractParkingLotCommandRunner;
import com.test.vehicleParkingLot.service.ParkingLotCommandFeedByConsole;
import com.test.vehicleParkingLot.service.ParkingLotCommandFeedByFile;

public class VehicleParkingLot {

	private static AbstractParkingLotCommandRunner parkingLotCommandRunner = null;

	public static void main(String[] args) {

		if (args.length == 0) {
			parkingLotCommandRunner = new ParkingLotCommandFeedByConsole();
		} else {
			parkingLotCommandRunner = new ParkingLotCommandFeedByFile(args[0]);
		}

		parkingLotCommandRunner.commandFeeder();
	}
	
}
