package com.beyonder.authservice.dto;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class JobDTO implements Serializable {
    private Long id;
    private String name;

}
