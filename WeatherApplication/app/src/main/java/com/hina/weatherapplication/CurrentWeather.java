package com.hina.weatherapplication;

public class CurrentWeather {
    final String location;
    final int conditionId;
    final String weatherCondition;
    final double tempKelvin;
    final int humidity;
    final double feelslike;
    final int pressure;
    final long sunset;
    final long sunrise;
    final String description;
    final long date;


    public CurrentWeather(final String location,
                          final int conditionId,
                          final String weatherCondition,
                          final double tempKelvin,
                          final int humidity,
                          final double feelslike,
                          final int pressure,
                          final long sunset,
                          final long sunrise,
                          final String description,
                          final long date) {
        this.location = location;
        this.conditionId = conditionId;
        this.weatherCondition = weatherCondition;
        this.tempKelvin = tempKelvin;
        this.humidity=humidity;
        this.feelslike=feelslike;
        this.pressure=pressure;
        this.sunset=sunset;
        this.sunrise=sunrise;
        this.description=description;
        this.date=date;
    }

    public int getTempFahrenheit() {
        return (int) (tempKelvin * 9/5 - 459.67);
    }

    public int getTempCelsius() {return (int) ((getTempFahrenheit() - 32) * 5/9);}

    public int getFeelFahrenheit(){return (int ) (feelslike * 9/5 - 459.67);}
    public int getFeelCelsius() {return (int) ((getFeelFahrenheit() - 32) * 5/9);}
}
