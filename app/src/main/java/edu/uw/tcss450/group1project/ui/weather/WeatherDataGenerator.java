///*
// * TCSS450 Mobile Applications
// * Fall 2021
// */
//
//package edu.uw.tcss450.group1project.ui.weather;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
///**
// * WeatherDataGenerator is a class for generating static weather data for the purpose of initial
// * UI display.
// *
// * @author Parker Rosengreen
// * @version Fall 2021
// */
//public class WeatherDataGenerator {
//
//    /**
//     * Return a list of weather data for a "dummy" 24-hour forecast
//     *
//     * @return the list of weather data
//     */
//    public static List<WeatherData> get24HrForecast() {
//        List<WeatherData> list = new ArrayList<>();
//        Random rand = new Random();
//        int currHour = 12;
//        boolean am = false;
//        list.add(new WeatherData("Now", 50, 0));
//        for (int i = 1; i <= 24; i++) {
//            currHour = currHour + 1 == 12 ? 12 : (currHour + 1) % 12;
//            am = currHour == 12 ? !am : am;
//            list.add(new WeatherData(currHour + (am ? "AM" : "PM"),
//                    rand.nextInt(11) + 40,
//                    rand.nextInt(2)));
//        }
//        return list;
//    }
//
//    /**
//     * Return a list of weather data for a "dummy" 10-day forecast
//     *
//     * @return the list of weather data
//     */
//    public static List<WeatherData> get10DayForecast() {
//        List<WeatherData> list = new ArrayList<>();
//        Random rand = new Random();
//        list.add(new WeatherData("Today",
//                50, 1));
//        list.add(new WeatherData("Fri",
//                rand.nextInt(11) + 40, rand.nextInt(2)));
//        list.add(new WeatherData("Sat",
//                rand.nextInt(11) + 40, rand.nextInt(2)));
//        list.add(new WeatherData("Sun",
//                rand.nextInt(11) + 40, rand.nextInt(2)));
//        list.add(new WeatherData("Mon",
//                rand.nextInt(11) + 40, rand.nextInt(2)));
//        list.add(new WeatherData("Tue",
//                rand.nextInt(11) + 40, rand.nextInt(2)));
//        list.add(new WeatherData("Wed",
//                rand.nextInt(11) + 40, rand.nextInt(2)));
//        list.add(new WeatherData("Thu",
//                rand.nextInt(11) + 40, rand.nextInt(2)));
//        list.add(new WeatherData("Fri",
//                rand.nextInt(11) + 40, rand.nextInt(2)));
//        list.add(new WeatherData("Sat",
//                rand.nextInt(11) + 40, rand.nextInt(2)));
//        return list;
//    }
//}
//
//
