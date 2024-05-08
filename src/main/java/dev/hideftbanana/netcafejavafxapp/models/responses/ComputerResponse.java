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
public class ComputerResponse {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("configuration")
    private String configuration;
    @JsonProperty("pricePerHour")
    private float pricePerHour;
    @JsonProperty("room")
    private RoomResponse room;
}
