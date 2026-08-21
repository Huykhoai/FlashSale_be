package org.erp.flashsale_be.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {
    private int status;
    private String message;
}
