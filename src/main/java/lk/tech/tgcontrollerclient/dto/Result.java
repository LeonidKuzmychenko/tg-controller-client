package lk.tech.tgcontrollerclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Result {
    private String command;
    private Object data;
}
