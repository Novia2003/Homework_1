package org.example.converter;

import org.example.dto.City;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

public class XmlConverter {

    private static final Logger logger = LoggerFactory.getLogger(XmlConverter.class);

    public static String toXML(City city) {
        try {
            logger.info("Starting conversion of city to XML: {}", city);
            JAXBContext context = JAXBContext.newInstance(City.class);

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            StringWriter writer = new StringWriter();
            marshaller.marshal(city, writer);

            String xml = writer.toString();
            logger.info("Successfully converted city to XML: {}", xml);

            return xml;
        } catch (JAXBException e) {
            logger.error("Error converting city to XML: {}", city, e);

            return null;
        }
    }

}
