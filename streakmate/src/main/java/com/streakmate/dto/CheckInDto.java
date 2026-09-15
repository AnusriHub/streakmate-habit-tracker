package com.streakmate.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckInDto {

    private Long participantId;

    @Size(max = 300, message = "Note cannot exceed 300 characters")
    private String note;
}
