package org.example.converter;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.City;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

@Slf4j
public class XmlConverter {

    public static String toXML(City city) {
        try {
            log.info("Starting conversion of city to XML: {}", city);
            JAXBContext context = JAXBContext.newInstance(City.class);

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            StringWriter writer = new StringWriter();
            marshaller.marshal(city, writer);

            String xml = writer.toString();
            log.debug("Converted city to XML: {}", xml);

            return xml;
        } catch (JAXBException e) {
            log.error("Error converting city to XML: {}", city, e);

            return null;
        }
    }

}
