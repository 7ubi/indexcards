import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export default class LocalService {

  public static saveData(key: string, value: string) {
    sessionStorage.setItem(key, value);
  }

  public static getData(key: string): string {
    return sessionStorage.getItem(key) ? sessionStorage.getItem(key)! : '';
  }

  public static removeData(key: string) {
    sessionStorage.removeItem(key);
  }

  public static clearData() {
    sessionStorage.clear();
  }
}
