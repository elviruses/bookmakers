package org.example.bookmaker.service;

import org.example.bookmaker.entity.Zenit;

public interface ZenitService {

    void create(Zenit zenit);

    ForecastResponse findById(Long id);

    GetForecastsResponse searchForecasts(String search, Integer limit);

    DeleteForecastResponse deleteById(Long id);
}
