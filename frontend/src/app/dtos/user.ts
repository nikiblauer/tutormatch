
interface BaseUser {
  firstname: string;
  lastname: string;
  email: string;
}

export interface UserSubject {
  name: string;
  description: string;
  url: string;
  id: number,
  role: string;
}

export interface Subject {
  name: string;
  description: string;
  url: string;
  id: number
}

export interface CreateApplicationUserDto extends BaseUser {
  password: string;
  matrNumber: number;
}
export interface SendPasswordResetDto {
  email: string;
}
export interface PasswordResetDto {
  password: string;
  repeatPassword: string;
}

export interface ApplicationUserDto extends BaseUser {
  id: number;
  firstname: string;
  lastname: string;
  matrNumber: number;
  email: string;
  telNr: string;
  street: string;
  areaCode: number;
  city: string;
}

export class UserDetailWithSubjectsDto {
  firstname: string;
  lastname: string;
  email: string;
  telNr: string;
  street: string;
  areaCode: number;
  city: string;
  tutorSubjects: string[];
  traineeSubjects: string[];
}

export class ApplicationUserDetailDto implements BaseUser {
  id: number;
  firstname: string;
  lastname: string;
  email: string;
  telNr: string;
  street: string;
  areaCode: number;
  city: string;

  public static getAddressAsString(user: ApplicationUserDetailDto): string {

    let addressParts = [];
    if (user.street) {
      if (user.city || user.areaCode) {
        addressParts.push(user.street + ', ');
      } else {
        addressParts.push(user.street);
      }
    }

    if (user.city) addressParts.push(user.city);
    if (user.areaCode) addressParts.push(user.areaCode);

    let address = addressParts.join(' ');
    return address;
  }
}

export class UserProfile extends ApplicationUserDetailDto {
  matrNumber: number;
  subjects: UserSubject[];
}
