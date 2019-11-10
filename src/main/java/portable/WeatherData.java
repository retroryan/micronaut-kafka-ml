package portable;

public class WeatherData {

    /**
     * These 2 fields are read and written from the orignal data source but can not be in the json to the weather service ML
     * so we have to use 2 different representations of the JSON data
     */
    public Integer bike_id;
    public Integer duration;

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
    public Double prediction;

}
