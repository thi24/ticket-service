package com.benevolo.rest.params;

import jakarta.ws.rs.QueryParam;

import java.time.LocalDate;
import java.util.List;

public class BookingSearchParams {

    @QueryParam("term")
    public String term;

    @QueryParam("dateFrom")
    public LocalDate dateFrom;

    @QueryParam("dateTo")
    public LocalDate dateTo;

    @QueryParam("priceFrom")
    public Integer priceFrom;

    @QueryParam("priceTo")
    public Integer priceTo;

    @QueryParam("status")
    public List<Integer> status;

}
