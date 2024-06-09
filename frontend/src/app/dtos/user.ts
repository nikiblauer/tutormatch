
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

export interface CreateStudentDto extends BaseUser {
  password: string; 
  repeatPassword: string;
  matrNumber: number;
}
export interface SendPasswordResetDto {
  email: string;
}
export interface PasswordResetDto {
  password: string;
  repeatPassword: string;
}

export class StudentSubjectInfoDto {
  firstname: string;
  lastname: string; 
  matrNumber: number;
  email: string;
  telNr: string;
  street: string;
  areaCode: number;
  city: string;
  tutorSubjects: string[];
  traineeSubjects: string[];
}

export class StudentDto implements BaseUser {
  id: number;
  firstname: string;
  lastname: string;
  matrNumber: number;
  email: string;
  telNr: string;
  street: string;
  areaCode: number;
  city: string; 
  verified: boolean; 

  public static getAddressAsString(user: StudentDto): string {

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

export class UserProfile extends StudentDto {
  matrNumber: number;
  subjects: UserSubject[];
}
