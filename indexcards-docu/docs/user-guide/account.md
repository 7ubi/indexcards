# Account

All projects and index cards belong to a user account, so you need to sign up and log in before using the app.

## Signup

On the signup page (`/signup`) enter a username, first name, last name and a password (twice). All fields are
required and the username must not be taken by another user. After the account was created you are redirected to the
login page.


## Login

Log in on `/login` with your username and password. The app then stores an access token (a JWT) in the browser's
local storage and sends it with every request. The token is valid for **24 hours**; after it expires, or if the
backend rejects it, you are logged out automatically and redirected to the login page.

Pages other than login, signup and the privacy policy can only be opened while logged in.

## Logout

Open the account menu (the account icon in the top right corner) and choose **Logout**. This removes the token from
the browser.

## Delete account

Open the account menu and choose **Delete account**. To confirm, you have to enter your password again.

!!! warning
    Deleting your account is permanent and cannot be undone.

The following data is deleted:

- your user account (username, first and last name, password hash),
- all of your projects,
- all index cards in these projects,
- your learning progress (ratings, rating history and review dates),
- uploaded images that are embedded in your index cards (unless another user's card references the same image).

## Language

The app is available in English and German. On the first visit the language is chosen based on the browser language
(German if the browser is set to German, English otherwise) and remembered in local storage.

## Privacy policy

The privacy policy is available at `/privacy` and linked from the footer and the signup page.
