package org.example.converter;

import org.example.dto.City;
import org.example.parser.JsonParser;
import org.example.writer.CustomFileWriter;

public class JsonToXmlConverter {

    public static void convertCityFromJsonToXml(String jsonFilePath) {
        City city = JsonParser.parseCity(jsonFilePath);

        if (city == null) {
            return;
        }

        String xml = XmlConverter.toXML(city);

        if (xml == null) {
            return;
        }

        String xmlFilePath = jsonFilePath.replace(".json", ".xml");
        CustomFileWriter.writeToFile(xml, xmlFilePath);
    }

}
