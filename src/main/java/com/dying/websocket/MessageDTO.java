package com.dying.websocket;

import lombok.Data;

@Data
public class MessageDTO {
    private Long receiverId;
    private String content;
}