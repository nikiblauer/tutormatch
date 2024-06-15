
export class ChatMessageDto {
  chatRoomId: string;
  senderId: number;
  recipientId: number;
  content: string;
  timestamp: Date;
}

export class CreateChatRoomDto {
  recipientId: number;
}

export class ChatRoomDto {
  chatRoomId: string;
  senderId: number;
  recipientId: number;
  senderFirstName: string;
  recipientFirstName: string;
  senderLastName: string;
  recipientLastName: string;
}

export class WebSocketErrorDto {
  errorMsg: string;
}

export class ReportChatRoomDto {
  chatroom: ChatRoomDto;
  reason: string;
}
