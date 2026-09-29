# AI Card Generation

Index cards can be generated from your notes or a PDF with Gemini by Google. The feature uses **your own Gemini API
key**: Google bills the usage directly to your Google account (or it counts against the free tier), the app does not
charge anything.

!!! note
    The feature is only available if the server operator has configured `AI_KEY_ENCRYPTION_SECRET`
    (see [Deployment](../operations/deployment.md#ai-card-generation)). Otherwise the button and the account section
    are hidden.

## Saving your API key

1. Create an API key in [Google AI Studio](https://aistudio.google.com/apikey) (it usually starts with `AIza`).
2. Open the [account page](account.md#account-page) and enter it under **AI card generation**.
3. Click **Save key**. The key is checked with Google before it is saved.

The key is stored encrypted (AES-256-GCM) and only used for your own generations. It is never shown again; the account
page only shows its last four characters. To replace it, save a new key. **Delete key** removes it from the server.
Deleting your account also deletes the key.

## Generating cards

1. Open a project and click the **Generate index cards with AI** button (✨). It is not shown for archived projects.
2. Paste your notes and/or add a PDF (at most 10 MB and 20 pages).
3. Choose the maximum number of cards (up to 30) and click **Generate suggestions**. This can take up to two minutes.
4. Review the suggestions: edit, select or deselect cards. AI can make mistakes, so check every card.
5. Click **Save** to add the selected cards to the project as new, unrated cards.

Nothing is saved until you click **Save**. Only one generation per user can run at a time.

!!! warning
    Your notes and the PDF are sent to Google (USA). Do not include personal data of yourself or others. On the free
    tier of the Gemini API, Google may use the content to improve its products.

## Errors

| Message                              | Cause                                                                     |
|--------------------------------------|---------------------------------------------------------------------------|
| The API key is invalid               | Google rejected the key (revoked or mistyped). Save a new key.            |
| Billing problem                      | Your Google billing account has no credit left.                           |
| Rate limit reached                   | Quota or rate limit of your key reached. Try again later.                 |
| Timeout / unavailable                | Gemini did not answer in time or is overloaded. Try again later.          |
| No cards could be created            | The input did not contain enough content, or Gemini blocked the request.  |
