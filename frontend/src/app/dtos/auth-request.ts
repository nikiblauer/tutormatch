export class AuthRequest {
  constructor(
    public email: string,
    public password: string
  ) {
    this.email = email.trim();
  }
}
