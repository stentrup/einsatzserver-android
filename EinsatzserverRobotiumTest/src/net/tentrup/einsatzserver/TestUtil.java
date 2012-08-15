package net.tentrup.einsatzserver;

import net.tentrup.einsatzserver.model.BookingState;
import net.tentrup.einsatzserver.model.Operation;

import org.joda.time.LocalDate;

public class TestUtil {

	public static Operation createOperation(int id, String description, String date, BookingState bookingState) {
		Operation operation = new Operation();
		operation.setId(id);
		operation.setDescription(description);
		operation.setDate(LocalDate.parse(date));
		operation.setBookingState(bookingState);
		return operation;
	}

}
