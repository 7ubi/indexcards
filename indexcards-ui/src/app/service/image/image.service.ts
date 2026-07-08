import { Injectable, inject } from '@angular/core';
import HttpService from '../http/http.service';
import { ImageUploadResponse } from '../../app.responses';

@Injectable({
  providedIn: 'root',
})
export class ImageService {
  private httpService = inject(HttpService);

  public upload(
    blob: Blob,
    subscribe: (response: ImageUploadResponse) => void,
    error?: () => void,
  ): void {
    const formData = new FormData();
    formData.append('file', blob, 'image.jpg');

    this.httpService.postFormData<ImageUploadResponse>('/api/images', formData, subscribe, error);
  }
}

/**
 * Resizes and re-encodes an image file to JPEG, capping its longest side at maxDimension,
 * so pasted screenshots/photos don't bloat the upload payload.
 */
export function compressImage(file: File, maxDimension = 1600, quality = 0.82): Promise<Blob> {
  return new Promise((resolve, reject) => {
    const image = new Image();
    const objectUrl = URL.createObjectURL(file);

    image.onload = () => {
      URL.revokeObjectURL(objectUrl);

      const scale = Math.min(1, maxDimension / Math.max(image.width, image.height));
      const width = Math.round(image.width * scale);
      const height = Math.round(image.height * scale);

      const canvas = document.createElement('canvas');
      canvas.width = width;
      canvas.height = height;

      const context = canvas.getContext('2d');
      if (!context) {
        reject(new Error('Canvas 2D context is not available'));
        return;
      }

      // Fill white first so transparent PNGs don't turn black once re-encoded as JPEG.
      context.fillStyle = '#ffffff';
      context.fillRect(0, 0, width, height);
      context.drawImage(image, 0, 0, width, height);

      canvas.toBlob(
        (blob) => {
          if (blob) {
            resolve(blob);
          } else {
            reject(new Error('Failed to encode compressed image'));
          }
        },
        'image/jpeg',
        quality,
      );
    };

    image.onerror = () => {
      URL.revokeObjectURL(objectUrl);
      reject(new Error('Failed to load pasted image'));
    };

    image.src = objectUrl;
  });
}
