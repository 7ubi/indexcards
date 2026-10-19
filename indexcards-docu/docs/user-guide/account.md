# Account

All projects and index cards belong to a user account, so you need to sign up and log in before using the app.

## Login

Click **Sign in or create account** on `/login`. You are redirected to the app's login service (Authentik), where you
can log in with your username and password, create a new account via **Sign up** (username, name, email address and
password; the account becomes usable once you open the confirmation link sent to your email address), or log in with Google (on the first Google login you choose a username once). Your Index Cards
account is created automatically on the first login, using your name as first and last name (the last word is the
surname). Password accounts and Google accounts are separate accounts.

Accounts created before an email address was required are asked for one on the next login and have to confirm it
before they can continue. If you forget your password, contact the operator; there is no reset by email yet. The app stores the access token for the
current browser tab only; when it expires (after 24 hours) or the backend rejects it, you are logged out and redirected
to the login page.

Pages other than login and the privacy policy can only be opened while logged in.

## Two-factor authentication

Under **Two-factor authentication** on the account page you can add a second factor that the login asks for after your
password: an authenticator app (e.g. Aegis, Google Authenticator), a passkey or security key, or a code sent to your
email address. Setting one up opens the login service, which brings you back to the account page afterwards. You can add
several second factors and remove them there at any time. If you lose access to all of them, contact the operator.

## Logout

Open the account menu (the account icon in the top right corner) and choose **Logout**. This removes the token from
the browser. Logging out also ends your session at the login service, so the next login asks for your credentials again.

## Account page

Open the account menu and choose **Account** (`/account`). The page shows your first and last name (read-only) and
lets you change your username or delete your account.

### Change username

Enter the new username, then click **Change username**. The new username must not be empty, must be at most 100
characters long and must not be taken by another user. Your login at Authentik is not affected, so you keep logging in
with the same credentials.

### Settings

**Show "due today" reminder** controls whether the dialog listing the index cards due today appears when you open
your projects. It is enabled by default. You can also turn it off directly in the dialog with **Don't show this
again**. The setting is stored in the browser's local storage, so it applies to this
browser only and is kept after logging out.

## Delete account

Deleting your account is done at the bottom of the account page. To confirm, you have to type your username.

!!! warning
    Deleting your account is permanent and cannot be undone.

The following data is deleted:

- your user account (username, first and last name),
- all of your projects,
- all index cards in these projects,
- your learning progress (ratings, rating history and review dates),
- uploaded images that are embedded in your index cards (unless another user's card references the same image),
- your login account (username and password, or the link to your Google account).

If the login service is unavailable at that moment, nothing is deleted and you can try again later.

## Language

The app is available in English and German. On the first visit the language is chosen based on the browser language
(German if the browser is set to German, English otherwise) and remembered in local storage.

## Privacy policy

The privacy policy is available at `/privacy` and linked from the footer and the login page.
