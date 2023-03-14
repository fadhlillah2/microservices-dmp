package com.beyonder.authservice.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class JobList<T> {
    private Long id;
    private String name;
//    private Integer star;
    private T review;

}
