package com.techelevator.dao;

import com.techelevator.model.Site;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.List;

public interface SiteDAO {

    List<Site> getSitesThatAllowRVs(int parkId);

    List<Site> getAvailableSites (int parkId);

    List<Site> getAvailableSitesDateRange (int parkId, LocalDate startDate, LocalDate endDate);
}
