package ru.otus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// todo тут можно сделать при помощи record
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {
    private String street;
}
