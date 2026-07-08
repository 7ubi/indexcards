import { Pipe, PipeTransform } from '@angular/core';
import { marked } from 'marked';

// Matches LaTeX math regions so they can be protected from markdown syntax (e.g. `_` and `*`
// inside math being misread as emphasis markers) before the markdown parser runs.
const MATH_PATTERN =
  /\$\$[\s\S]+?\$\$|\\\[[\s\S]+?\\\]|\\\([\s\S]+?\\\)|\$(?!\s)(?:\\\$|[^$\n])+?(?<!\s)\$/g;

@Pipe({
  name: 'markdownMath',
  standalone: true,
})
export class MarkdownMathPipe implements PipeTransform {
  transform(value?: string | null): string {
    if (!value) {
      return '';
    }

    const mathSegments: string[] = [];
    const withPlaceholders = value.replace(MATH_PATTERN, (match) => {
      const index = mathSegments.push(match) - 1;
      return `@@MATH${index}@@`;
    });

    const html = marked.parse(withPlaceholders, { breaks: true, async: false });

    return html.replace(/@@MATH(\d+)@@/g, (_, index) => escapeHtml(mathSegments[Number(index)]));
  }
}

function escapeHtml(text: string): string {
  return text.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}
