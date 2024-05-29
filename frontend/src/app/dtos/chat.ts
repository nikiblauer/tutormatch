
export class ChatMessageDto {
  chatRoomId: string;
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
  chatId: string;
  senderId: number;
  recipientId: number;
}
