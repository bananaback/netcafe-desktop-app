package dev.hideftbanana.netcafejavafxapp.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComputersResponse {
    @JsonProperty("computers")
    private List<ComputerResponse> computers;
}
