package com.techelevator.dao.jdbc;

import com.techelevator.dao.SiteDAO;
import com.techelevator.model.Site;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JDBCSiteDAO implements SiteDAO {

    private JdbcTemplate jdbcTemplate;

    public JDBCSiteDAO(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Site> getSitesThatAllowRVs(int parkId) {
        List<Site> sites = new ArrayList<>();
        String sql = "SELECT s.site_id, s.campground_id, s.site_number, s.max_occupancy," +
                " s.accessible, s.max_rv_length, s.utilities FROM site s " +
                "INNER JOIN campground c ON c.campground_id = s.campground_id " +
                "INNER JOIN park p ON p.park_id = c.park_id " +
                "WHERE s.max_rv_length !=0 AND c.park_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, parkId);
        while (results.next()){
            Site site = mapRowToSite(results);
            sites.add(site);
        }
        return sites;
    }

    @Override
    public List<Site> getAvailableSites(int parkId) {
        List<Site> sites = new ArrayList<>();
        String sql = "SELECT s.site_id, s.campground_id, s.site_number, " +
                "s.max_occupancy, s.accessible, s.max_rv_length, s.utilities FROM site s " +
                "INNER JOIN campground c ON c.campground_id = s.campground_id " +
                "WHERE park_id=? AND s.site_Id  NOT IN (SELECT r.site_id FROM reservation r " +
                "WHERE current_date BETWEEN from_date AND to_date)";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, parkId);
        while (results.next()){
            Site site = mapRowToSite(results);
            sites.add(site);
        }
        return sites;
    }

    @Override
    public List<Site> getAvailableSitesDateRange(int parkId, LocalDate startDate, LocalDate endDate) {
        List<Site> sites = new ArrayList<>();
        String sql = "SELECT s.site_id, s.campground_id, s.site_number, " +
                "s.max_occupancy, s.accessible, s.max_rv_length, s.utilities FROM site s " +
                "INNER JOIN campground c ON c.campground_id = s.campground_id " +
                "WHERE park_id=? AND s.site_Id NOT IN (SELECT r.site_id FROM reservation r " +
                "WHERE to_date<? OR from_date>?)";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, parkId, startDate, endDate);
        while (results.next()){
            Site site = mapRowToSite(results);
            sites.add(site);
        }
        return sites;
    }

    private Site mapRowToSite(SqlRowSet results) {
        Site site = new Site();
        site.setSiteId(results.getInt("site_id"));
        site.setCampgroundId(results.getInt("campground_id"));
        site.setSiteNumber(results.getInt("site_number"));
        site.setMaxOccupancy(results.getInt("max_occupancy"));
        site.setAccessible(results.getBoolean("accessible"));
        site.setMaxRvLength(results.getInt("max_rv_length"));
        site.setUtilities(results.getBoolean("utilities"));
        return site;
    }
}
