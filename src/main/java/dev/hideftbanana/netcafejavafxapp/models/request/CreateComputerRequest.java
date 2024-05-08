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
public class CreateComputerRequest {
    @JsonProperty("configuration")
    private String configuration;

    @JsonProperty("pricePerHour")
    private float pricePerHour;

    @JsonProperty("roomId")
    private long roomId;
}
