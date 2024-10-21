package ru.tbank.dto.cbr.item;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDTO {

    @JacksonXmlProperty(localName = "Name")
    private String name;

    @JacksonXmlProperty(localName = "EngName")
    private String engName;

    @JacksonXmlProperty(localName = "Nominal")
    private int nominal;

    @JacksonXmlProperty(localName = "ParentCode")
    private String parentCode;

    @JacksonXmlProperty(localName = "ISO_Num_Code")
    private String isoNumCode;

    @JacksonXmlProperty(localName = "ISO_Char_Code")
    private String isoCharCode;
}
