export interface SimpleStaticticsDto {
    registeredVerifiedUsers: number;
    registeredUnverifiedUsers: number;
    ratioOfferedNeededSubjects: number;
    openChatsPerUser: number;
}

export interface ExtendedStatisticsDto {
    topXofferedSubjects: string[];
    topXneededSubjects: string[];
    topXofferedAmount: number[];
    topXneededAmount: number[];
}

export interface CoverageSubjectsStatisticsDto {
    subjectInfo: string;
    numOfTutors: number;
    numOfTrainees: number;
    diff: number;
}