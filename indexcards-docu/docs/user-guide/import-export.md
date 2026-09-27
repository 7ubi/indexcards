# Import & Export

Index cards can be imported from and exported to CSV files on the project page. This makes it easy to move cards
between projects or to create many cards at once in a spreadsheet.

## Format

Each line is one index card with two columns: **question**, then **answer**. There is no header line.

```csv
"What is the capital of France?","Paris"
"What does $E = mc^2$ describe?","Mass-energy equivalence"
Plain question without quotes,Plain answer
```

- Values may be wrapped in double quotes. This is required if a value contains a comma.
- A double quote inside a quoted value is written as two double quotes (`""`).
- Leading and trailing whitespace of each value is removed.
- Lines with fewer than two columns are skipped.
- Line breaks inside a value are **not** supported; every line is a separate card.

## Import

Click the import button on the project page and choose a CSV file. All cards from the file are added to the project as
new, unrated cards. Existing cards are not changed or replaced, so importing the same file twice creates duplicates.

## Export

Click the export button to download all cards of the project as `<project name>.csv`. Every value is quoted, so the file
can be imported again without changes.

The export only contains question and answer; ratings and review dates are not exported. Images are exported as their
Markdown link (`![image](/api/images/<id>)`) and keep working as long as the image exists on the server.
