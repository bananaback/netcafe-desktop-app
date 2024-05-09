package dev.hideftbanana.netcafejavafxapp.models.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComputerIdsRequest {

    private List<Long> computerIds;

    public List<Long> getComputerIds() {
        return computerIds;
    }

    public void setComputerIds(List<Long> computerIds) {
        this.computerIds = computerIds;
    }
}
