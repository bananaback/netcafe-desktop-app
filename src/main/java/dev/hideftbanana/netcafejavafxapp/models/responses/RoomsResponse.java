package dev.hideftbanana.netcafejavafxapp.models.responses;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomsResponse {
    @JsonProperty("rooms")
    private List<RoomResponse> rooms;
}
