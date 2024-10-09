package ru.tbank.dto.cbr.curs;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import ru.tbank.dto.cbr.valute.ValuteDTO;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "ValCurs")
public class ValCursDTO {

    @JacksonXmlProperty(localName = "Date")
    private String date;

    @JacksonXmlProperty(localName = "name")
    private String name;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Valute")
    private List<ValuteDTO> valutes;
}
