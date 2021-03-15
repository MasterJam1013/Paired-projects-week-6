package com.techelevator.dao.jdbc;

import com.techelevator.dao.ReservationDAO;
import com.techelevator.model.Reservation;
import com.techelevator.model.Site;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JDBCReservationDAO implements ReservationDAO {

    private JdbcTemplate jdbcTemplate;

    public JDBCReservationDAO(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public int createReservation(int siteId, String name, LocalDate fromDate, LocalDate toDate) {

        String sql = "INSERT INTO reservation( site_id, name, from_date, to_date)" +
                "VALUES(?, ?, ?, ?)";
        Long confirmationId = getNextReservationID();
        int reservationCreated = jdbcTemplate.update(sql, siteId, name, fromDate, toDate);
        if(reservationCreated == 1){
            System.out.println("Your reservation has been created, your confirmation ID is: " + confirmationId);
        }
        return reservationCreated;

    }

    @Override
    public List<Reservation> upcomingReservations(int parkID){
        ArrayList<Reservation> reservations= new ArrayList<>();
        String sql = "SELECT reservation_id, r.site_id, r.name, from_date, to_date, create_date FROM reservation r " +
                "INNER JOIN site s ON s.site_id = r.site_id " +
                "INNER JOIN campground c ON c.campground_id = s.campground_id " +
                "WHERE c.park_id = ? AND from_date BETWEEN CURRENT_DATE AND(CURRENT_DATE + 30)";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, parkID);
        while(results.next()) {
            Reservation reservation = mapRowToReservation(results);
            reservations.add(reservation);
        }
        return reservations;
    }

    private Reservation mapRowToReservation(SqlRowSet results) {
        Reservation r = new Reservation();
        r.setReservationId(results.getInt("reservation_id"));
        r.setSiteId(results.getInt("site_id"));
        r.setName(results.getString("name"));
        r.setFromDate(results.getDate("from_date").toLocalDate());
        r.setToDate(results.getDate("to_date").toLocalDate());
        r.setCreateDate(results.getDate("create_date").toLocalDate());
        return r;
    }
    private long getNextReservationID(){
        SqlRowSet nextIdResult = jdbcTemplate.queryForRowSet("SELECT nextval('reservation_reservation_id_seq')");
        if(nextIdResult.next()){
            return nextIdResult.getLong(1);
        } else{
            throw new RuntimeException("SOMETHING WENT WRONG WHILE GETTING THE RESERVATION ID");
        }
    }


}
