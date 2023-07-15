import { Injectable } from '@angular/core';
import * as CryptoJS from 'crypto-js';

@Injectable({
  providedIn: 'root'
})
export class LocalService {

  private static key = 'SECRET';

  public static saveData(key: string, value: string) {
    sessionStorage.setItem(key, value);
  }

  public static getData(key: string) {
    return sessionStorage.getItem(key)
  }
  public static removeData(key: string) {
    sessionStorage.removeItem(key);
  }

  public static clearData() {
    sessionStorage.clear();
  }

  private static encrypt(txt: string): string {
    return CryptoJS.AES.encrypt(txt, this.key).toString();
  }

  private static decrypt(txtToDecrypt: string) {
    return CryptoJS.AES.decrypt(txtToDecrypt, this.key).toString(CryptoJS.enc.Utf8);
  }

  public static saveEncryptedData(key: string, value: string) {
    sessionStorage.setItem(key, this.encrypt(value));
  }

  public static getEncryptedData(key: string) {
    const data = sessionStorage.getItem(key)|| "";
    return this.decrypt(data);
  }
}
