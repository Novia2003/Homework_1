package ru.tbank.dto.cbr.valute;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import ru.tbank.dto.cbr.item.ItemDTO;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "Valuta")
public class ValuteFullDTO {

    @JacksonXmlProperty(localName = "name")
    private String name;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Item")
    private List<ItemDTO> items;
}
