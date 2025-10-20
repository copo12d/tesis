package com.tesisUrbe.backend.entities.setting;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UniversitySetting {

    @JsonProperty("legal_name")
    private String legalName;

    @JsonProperty("tax_id")
    private TaxId taxId;

    @JsonProperty("address_1")
    private String address1;

    @JsonProperty("address_2")
    private String address2;

    @JsonProperty("address_3")
    private String address3;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("email")
    private String email;

    @JsonProperty("logo_path")
    private String logoPath;

    @Data
    public static class TaxId {
        @JsonProperty("type")
        private String type;

        @JsonProperty("number")
        private String number;
    }
}
