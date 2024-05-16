
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

export class ApplicationUserDetailDto{
  firstname: string;
  lastname: string;
  email: string;
  telNr: string;
  street: string;
  areaCode: number;
  city: string;
}
