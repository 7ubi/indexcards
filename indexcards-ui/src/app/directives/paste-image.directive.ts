import { Directive, ElementRef, HostListener, inject } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ImageService, compressImage } from '../service/image/image.service';

@Directive({
  selector: 'textarea[appPasteImage]',
})
export class PasteImageDirective {
  private el = inject(ElementRef<HTMLTextAreaElement>);
  private ngControl = inject(NgControl, { self: true });
  private imageService = inject(ImageService);

  @HostListener('paste', ['$event'])
  onPaste(event: ClipboardEvent): void {
    const items = event.clipboardData?.items;
    if (!items) {
      return;
    }

    const imageItem = Array.from(items).find((item) => item.type.startsWith('image/'));
    if (!imageItem) {
      return;
    }

    const file = imageItem.getAsFile();
    if (!file) {
      return;
    }

    event.preventDefault();
    this.handleImage(file);
  }

  private handleImage(file: File): void {
    const placeholder = `![uploading](uploading-${crypto.randomUUID()})`;
    this.insertAtCursor(placeholder);

    compressImage(file).then(
      (blob) => {
        this.imageService.upload(
          blob,
          (response) => {
            this.replacePlaceholder(placeholder, `![image](/api/images/${response.imageId})`);
          },
          () => {
            this.replacePlaceholder(placeholder, '');
          },
        );
      },
      () => {
        this.replacePlaceholder(placeholder, '');
      },
    );
  }

  private insertAtCursor(text: string): void {
    const textarea = this.el.nativeElement;
    const start = textarea.selectionStart ?? textarea.value.length;
    const end = textarea.selectionEnd ?? textarea.value.length;
    const current = this.ngControl.control?.value ?? '';

    const updated = current.slice(0, start) + text + current.slice(end);
    this.ngControl.control?.setValue(updated);

    const cursor = start + text.length;
    setTimeout(() => textarea.setSelectionRange(cursor, cursor));
  }

  private replacePlaceholder(placeholder: string, replacement: string): void {
    const current: string = this.ngControl.control?.value ?? '';
    this.ngControl.control?.setValue(current.replace(placeholder, replacement));
  }
}
