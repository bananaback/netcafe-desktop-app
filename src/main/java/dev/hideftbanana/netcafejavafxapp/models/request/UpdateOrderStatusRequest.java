package dev.hideftbanana.netcafejavafxapp.models.request;

import dev.hideftbanana.netcafejavafxapp.models.responses.OrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {
    private OrderStatusEnum status;
}