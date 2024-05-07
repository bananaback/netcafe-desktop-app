package dev.hideftbanana.netcafejavafxapp.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("price")
    private float price;
    @JsonProperty("remainQuantity")
    private int remainQuantity;
    @JsonProperty("productCategory")
    private ProductCategoryResponse productCategory;
    @JsonProperty("productImageLink") // Add imageLink property
    private String imageLink;
}