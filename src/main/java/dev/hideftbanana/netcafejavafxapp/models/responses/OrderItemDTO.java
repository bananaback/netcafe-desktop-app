package dev.hideftbanana.netcafejavafxapp.models.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private int quantity;
    private float singlePrice;
    private Long productId;
    private Long orderId;
}
