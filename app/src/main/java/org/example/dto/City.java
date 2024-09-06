package org.example.dto;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement
public class City {
    private String slug;
    private Coordinates coords;
}
