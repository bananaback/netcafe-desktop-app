package dev.hideftbanana.netcafejavafxapp.models.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductCategoryRequest {
    private String categoryName;
    private String imageLink;
}
