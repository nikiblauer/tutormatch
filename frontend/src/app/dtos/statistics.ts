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

export interface CoverageStatisticsDto {
    mostRequestedSubjectsWithoutCoverage: string[];
    mostOfferedSubjectsWithoutCoverage: string[];

    numberOfStudentsRequestedSubjects: string[];
    numberOfStudentsOfferedSubjects: string[];
}