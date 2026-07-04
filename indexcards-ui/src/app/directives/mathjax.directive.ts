import {AfterViewInit, Directive, ElementRef, OnDestroy} from '@angular/core';

declare let MathJax: any;

@Directive({
  selector: '[appMathjax]'
})
export class MathjaxDirective implements AfterViewInit, OnDestroy {
  private observer?: MutationObserver;
  private isTypesetting = false;
  private debounceTimer: any;

  constructor(private el: ElementRef) {
  }

  ngAfterViewInit(): void {
    // Initial typeset (if MathJax already loaded)
    this.typeset();

    // Observe changes (e.g. [innerHTML] updates) and re-typeset
    this.observer = new MutationObserver(() => {
      // If we're currently running MathJax, skip immediate re-entry
      if (this.isTypesetting) {
        return;
      }

      // debounce rapid mutations (typing, editor updates)
      if (this.debounceTimer) {
        clearTimeout(this.debounceTimer);
      }
      this.debounceTimer = setTimeout(() => this.typeset(), 150);
    });

    this.observer.observe(this.el.nativeElement, {
      childList: true,
      subtree: true,
      characterData: true
    });
  }

  private typeset() {
    // Clear any pending debounce timer
    if (this.debounceTimer) {
      clearTimeout(this.debounceTimer);
      this.debounceTimer = null;
    }

    // If MathJax is not yet loaded, retry after a short delay (single timer)
    if (typeof (window as any).MathJax === 'undefined') {
      if (!this.debounceTimer) {
        this.debounceTimer = setTimeout(() => this.typeset(), 200);
      }
      return;
    }

    // Avoid concurrent typesetting
    if (this.isTypesetting) {
      return;
    }

    this.isTypesetting = true;
    try {
      MathJax.typesetPromise([this.el.nativeElement])
        .catch((err: any) => console.error('MathJax typeset failed', err))
        .finally(() => {
          this.isTypesetting = false;
        });
    } catch (e) {
      console.error('MathJax typeset failed', e);
      this.isTypesetting = false;
    }
  }

  ngOnDestroy(): void {
    if (this.observer) {
      this.observer.disconnect();
    }
    if (this.debounceTimer) {
      clearTimeout(this.debounceTimer);
    }
  }
}
