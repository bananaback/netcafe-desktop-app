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
public class RoomResponse {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;
}
