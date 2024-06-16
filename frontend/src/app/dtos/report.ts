import {ChatRoomDto} from "./chat";

export class ReportDto {
  firstnameReported: string;
  lastNameReported: string;
  reporterId: number;
  reportedId: number;
  reason: string;
  id: number;
  firstnameReporter: string;
  lastnameReporter: string;
  feedback: string;
  chatRoomId: string;

}

export class ReportChatRoomDto {
  chatId: string;
  reason: string;
}
