export interface Page<T> {
    content: T[];
    pageable: any; // Define the type of pageable object
    totalPages: number;
    totalElements: number;
    last: boolean;
    size: number;
    number: number;
    sort: any; // Define the type of sort object
    numberOfElements: number;
    first: boolean;
    empty: boolean;
}