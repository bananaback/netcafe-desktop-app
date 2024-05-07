package dev.hideftbanana.netcafejavafxapp.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("price")
    private float price;
    @JsonProperty("remainQuantity")
    private int remainQuantity;
    @JsonProperty("productImageLink")
    private String productImageLink;
    @JsonProperty("categoryId")
    private Long categoryId;
}
