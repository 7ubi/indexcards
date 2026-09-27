# Index Cards

An index card has a **question** (front) and an **answer** (back). Both are required. When studying, the card is
shown with the question side up and flipped by clicking or tapping on it.

## Create and edit

On the project page click **Create index card**. The editor has an **Edit** and a **Preview** tab, so you can check
how Markdown, math and images are rendered before saving. Existing cards are edited the same way from the project
page.

Editing a card only changes its text; its rating and review schedule stay the same.

## Markdown

Question and answer are rendered as [Markdown](https://www.markdownguide.org/basic-syntax/). Single line breaks are
kept, so you do not need two spaces at the end of a line.

```markdown
**Photosynthesis** converts:

- light energy
- water and CO₂

into *glucose* and oxygen.
```

## Math

LaTeX math is rendered with [MathJax](https://www.mathjax.org/). The following delimiters are supported:

| Type          | Syntax                        |
|---------------|-------------------------------|
| Inline        | `$a^2 + b^2 = c^2$` or `\(a^2 + b^2 = c^2\)` |
| Display/block | `$$\int_0^1 x\,dx$$` or `\[\int_0^1 x\,dx\]`  |

Math is protected from Markdown, so characters like `_` and `*` inside a formula are not treated as emphasis.

## Images

To add an image, copy it to the clipboard and **paste** it into the question or answer field. The image is

1. resized in the browser to at most 1600 px on its longest side and converted to JPEG,
2. uploaded to the server (max. 5 MB),
3. inserted into the text as Markdown: `![image](/api/images/<id>)`.

While the upload is running a placeholder is shown. If the upload fails the placeholder is removed again.

!!! note
    Images are stored under a random id and can be viewed by anyone who knows their link. Do not upload images with
    sensitive content.

## Delete

Cards can be deleted from the project page. This also deletes the card's rating history.
