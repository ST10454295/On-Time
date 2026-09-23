
# On-Time

On-Time is an Android time-management app for students. It brings
academic responsibilities, daily chores and personal activities into
one place, using a High/Medium/Low importance system to flag schedule
conflicts and help students prioritise.

## Features in this prototype

- **Register and log in** — Firebase Authentication. Password hashing
  is handled by Firebase rather than custom code.
- **Settings** — saves default activity duration and theme preference
  to a hosted Firestore database via a REST API call (Retrofit).
- **Add Activity** — title, description, type (Assignment, Test,
  Practical, Studying, Cleaning, Shopping, Exercise, Family Activity,
  Social Activity, Personal Appointment, Entertainment, Other), date,
  start/end time and importance level. New activities are checked
  against existing ones on the same day; on a conflict, the user sees
  the conflicting importance level and can choose to change the time
  or save anyway.
- **Conflict-checking logic** — written as plain Kotlin with no
  Android dependencies, so it's independently unit tested.


## Changes from the original planning document

**Backend: Firebase instead of PHP/MySQL.** The planning document i wrote
specifies a REST API built in PHP, backed by MySQL, tested locally through
XAMPP and hosted on InfinityFree. considering the time available for this
submission and challenges i had encountered, I implemented the REST-API-to-hosted-database requirement
using Firebase's REST endpoints  instead of building and
hosting a custom PHP API. This still satisfies the technical
requirement  the app makes RESTful HTTPS calls to a database hosted
outside the device  while being realistic to build, test and debug
in the time available. 

the prototype i had created , had a different colour from this one. i had a change of mind with the the colours, which might also change in part 3 .


## Still to build (deferred, will be included in part 3)

- Settings: notification preferences and reminder times (language
  selection is explicitly scoped for the final PoE in the planning
  document, so it's intentionally not here yet)
- Dashboard (Screen 4) and Calendar (Screen 7) — Home is currently a
  simple placeholder linking to Add Activity and Settings
- SSO, offline mode/synchronisation, and isiZulu/isiXhosa support 
  all explicitly scoped for the final PoE in the planning document
  

## Testing
 no conflict when activities don't
overlap, conflict detection on full and partial overlaps, picking the
most severe importance when multiple activities conflict, and
rejecting malformed time input instead of crashing.

## YOUTUBE DEMO LINK
https://youtube.com/shorts/SHy05MzxyCo?si=4L-n6yI_bgdbOMh9
