package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.Date;

public class ChatMessageDetailDto {
    private Long chatId;

    private Long senderId;
    private Long recipientId;
    private String content;
    private Date timestamp;
}
