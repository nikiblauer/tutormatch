export class SubjectCreateDto {
  title: String;
  type: String;
  number: String;
  semester: String;
  url: String;
  description: String;
}

export class SubjectDetailDto {
  title: String;
  type: String;
  number: String;
  semester: String;
  url: String;
  description: String;
  id: number;
}
