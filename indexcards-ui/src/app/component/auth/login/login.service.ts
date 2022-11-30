export class LoginService {
  readonly _token: string;
  readonly _type: string;
  readonly _id: string;
  readonly _username: string;

  constructor(token: string, type: string, id: string, username: string) {
    this._token = token;
    this._type = type;
    this._id = id;
    this._username = username;
  }

  get token(): string {
    return this._token;
  }

  get type(): string {
    return this._type;
  }

  get id(): string {
    return this._id;
  }

  get username(): string {
    return this._username;
  }
}
