# stoplaclop

Tobacco consumption tracking tool

## TASK AFTER CLONE
Launch ```sh setup-hooks.sh``` to set up git hooks.

## Features
### Features
* Registration
* Login (with JWT and HttpOnly Cookie sent to front-end)
* Refresh token ()
* Tobacco consumption tracking

### Routes
- POST /auth/register
- POST /auth/login
- POST /auth/refresh
- POST /logout
- GET /smoked
- POST /smoked

### Todo List
* forgotten-password

## How to contribute
### Branch
You cannot push or commit on branches dev or main. They are reserved for pull requests.

### Commits
Commits must explain what they are. They must start with :
* feat
* style
* fix
* config

### CI
CI is configured on pull request to branches "main" and "dev"

