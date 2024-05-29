
export class ChatMessageDto {
  chatRoomId: number;
  senderId: number;
  recipientId: number;
  content: string;
  timestamp: Date;
}

export class CreateChatRoomDto {
  senderId: number;
  recipientId: number;
}

export class ChatRoomDto {
  chatId: number;
  senderId: number;
  recipientId: number;
}
