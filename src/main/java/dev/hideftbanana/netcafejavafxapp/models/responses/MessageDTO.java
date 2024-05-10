package dev.hideftbanana.netcafejavafxapp.models.responses;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long id;

    private LocalDateTime sentTime;

    private String message;

    private Long senderId;

    private Long receiverId;
}
