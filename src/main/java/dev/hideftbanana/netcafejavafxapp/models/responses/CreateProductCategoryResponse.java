package dev.hideftbanana.netcafejavafxapp.models.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductCategoryResponse {
    private String message;
    private String errorMessage;
}
