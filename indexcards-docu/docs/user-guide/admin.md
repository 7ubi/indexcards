# Admin Analytics

Admins see an app-wide overview of how Indexcards is used. Open the account menu (the account icon in the top right
corner) and choose **Analytics** (`/admin`). The entry is only shown to admins; other users who open `/admin` are sent
back to their projects. See [Creating an admin](../operations/deployment.md#creating-an-admin) for how to grant the
role.

The page shows:

- **Totals**: number of users, active projects (with the number of archived projects below), index cards and ratings.
- **Current rating of all index cards**: how many cards are currently unrated, bad, ok and good.
- **Ratings per day** and **Active users per day** for the last 30 days. A user counts as active on a day when they
  rated at least one card. Hover over a bar to see the date and value.
- **Sign-ups per day** for the last 30 days. Accounts created before signup dates were recorded have no date and are
  not included.
- **Users**: one row per user with signup date, number of projects, index cards and ratings, and the time of their
  latest rating. Click a column header to sort. Admins are marked with an **Admin** label; every other user has a
  **Make admin** button.

## Making other users admins

Click **Make admin** in a user's row and confirm the dialog. The user gets the admin role immediately and can open
the analytics page, see all users and make further admins. There is no way to remove the role in the app; see
[Creating an admin](../operations/deployment.md#creating-an-admin) for how to revoke it in the database.

Days are based on the server's time zone.
