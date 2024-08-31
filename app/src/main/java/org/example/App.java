package org.example;

import org.example.converter.JsonToXmlConverter;

public class App {

    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        JsonToXmlConverter.convertCityFromJsonToXml("app/src/main/resources/cities/city-error.json");
        JsonToXmlConverter.convertCityFromJsonToXml("app/src/main/resources/cities/city.json");
    }

}
