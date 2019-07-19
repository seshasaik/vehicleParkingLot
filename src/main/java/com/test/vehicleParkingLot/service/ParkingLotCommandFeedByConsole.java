package com.test.vehicleParkingLot.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ParkingLotCommandFeedByConsole extends AbstractParkingLotCommandRunner {

	@Override
	public void commandFeeder() {
		// TODO Auto-generated method stub
		System.out.println("Please enter 'stop' to exit");
		System.out.println("Enter command \n");

		try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {

			while (true) {
				String nextLine = br.readLine();
				if (nextLine != null && nextLine.trim().length() != 0) {

					if (nextLine.equals("stop")) {
						break;
					} else {
						parseCommand(nextLine);
						System.out.println("Please enter 'stop' to exit");
						System.out.println("Enter command \n");
					}

				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
