package com.test.vehicleParkingLot.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class ParkingLotCommandFeedByFile extends AbstractParkingLotCommandRunner {

	private String filePath = "";

	public ParkingLotCommandFeedByFile(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public void commandFeeder() {
		// TODO Auto-generated method stub

		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath))))) {
			String command = "";
			while ((command = br.readLine()) != null) {
				parseCommand(command);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("File not found");

		} catch (IOException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
