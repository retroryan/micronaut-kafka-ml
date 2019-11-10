package portable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherDataML {

    public String end_station_id;
    public Double ts;
    public String day_of_week;
    public String start_station_id;
    public Double euclidean;
    public String loc_cross;
    public Double prcp;
    public Double max;
    public Double min;
    public Double temp;
    public Double dewp;

    /**
     *  The input weather data has several fields that  are read and written from the orignal data source but
     *  can not be in the json to the weather service ML
     *  so we have to use 2 different representations of the JSON data
     */
    public WeatherDataML(WeatherData weatherData) {
        end_station_id = weatherData.end_station_id;
        ts = weatherData.ts;
        day_of_week = weatherData.day_of_week;
        start_station_id = weatherData.start_station_id;
        euclidean = weatherData.euclidean;
        loc_cross = weatherData.loc_cross;
        prcp = weatherData.prcp;
        max = weatherData.max;
        min = weatherData.min;
        temp = weatherData.temp;
        dewp = weatherData.dewp;
    }
}
