package com.techelevator.dao.jdbc;

import com.techelevator.dao.ReservationDAO;
import com.techelevator.model.Reservation;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JDBCReservationDAOTests extends BaseDAOTests {

    private ReservationDAO dao;

    @Before
    public void setup() {
        dao = new JDBCReservationDAO(dataSource);
    }

    @Test
    public void createReservation_Should_ReturnNewReservationId() {
        int reservationCreated = dao.createReservation(9999,
                "TEST NAME",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3));

        assertEquals(1, reservationCreated);
    }

    @Test
    public void upcoming_reservations_returns_correct_reservations(){
        List<Reservation> actual = dao.upcomingReservations(99);
        assertEquals(2, actual.size());
    }

}
