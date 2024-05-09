package dev.hideftbanana.netcafejavafxapp.models.responses;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdersResponse {
    private List<OrderResponse> orders;
}
