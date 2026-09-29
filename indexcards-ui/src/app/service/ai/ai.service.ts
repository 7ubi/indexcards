import { Injectable, inject } from '@angular/core';
import HttpService from '../http/http.service';
import {
  AiStatusResponse,
  GeneratedCardResponse,
  GeneratedCardsResponse,
} from '../../app.responses';

/** Backend calls of the AI card generation. Errors are shown by HttpService's snackbar as `backend.<code>`. */
@Injectable({
  providedIn: 'root',
})
export class AiService {
  private httpService = inject(HttpService);

  getStatus(callback: (status: AiStatusResponse) => void, error?: () => void): void {
    this.httpService.get<AiStatusResponse>('/api/ai/status', callback, error);
  }

  saveApiKey(
    apiKey: string,
    callback: (status: AiStatusResponse) => void,
    error?: () => void,
  ): void {
    this.httpService.put<AiStatusResponse>('/api/ai/apiKey', { apiKey }, callback, error);
  }

  deleteApiKey(callback: (status?: AiStatusResponse) => void, error?: () => void): void {
    this.httpService.delete<AiStatusResponse>('/api/ai/apiKey', callback, error);
  }

  generate(
    projectId: string,
    notes: string,
    cardCount: number,
    file: File | undefined,
    callback: (response: GeneratedCardsResponse) => void,
    error?: () => void,
  ): void {
    const formData = new FormData();
    formData.append('projectId', projectId);
    formData.append('notes', notes);
    formData.append('cardCount', String(cardCount));
    if (file) {
      formData.append('file', file);
    }
    this.httpService.postFormData<GeneratedCardsResponse>(
      '/api/ai/generate',
      formData,
      callback,
      error,
    );
  }

  saveCards(
    projectId: string,
    cards: GeneratedCardResponse[],
    callback: () => void,
    error?: () => void,
  ): void {
    this.httpService.post<undefined>('/api/indexCard/bulk', { projectId, cards }, callback, error);
  }
}
