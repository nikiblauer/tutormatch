
export class CreateApplicationUserDto {
  password: string;
  firstname: string;
  lastname: string;
  matrNumber: number;
  email: string;
}

export class ApplicationUserDto {
  firstname: string;
  lastname: string;
  matrNumber: number;
  email: string;
  telNr: string;
}
