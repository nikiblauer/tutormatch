export interface SimpleStaticticsDto {
    registeredVerifiedUsers: number;
    ratioOfferedNeededSubjects: number;
    openChatsPerUser: number;
} 

export interface ExtendedStatisticsDto {
    topXofferedSubjects: string[]; 
    topXneededSubjects: string[]; 
    topXofferedAmount: number[];
    topXneededAmount: number[];
}