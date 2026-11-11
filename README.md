# Custom Events Plugin

**Build status: early development build, not the completed server plugin.**

This archive contains a Maven/Java 21 Paper project and an implemented saved-kit subsystem. It is not yet the full event plugin. The outstanding features are listed below so the archive is not mistaken for a finished release.

## Implemented in this archive

- `/create kit <kitname>` creates a saved kit from the player's storage inventory, armor, offhand, and ender chest. Saving does not consume the player's items.
- `/kit update <kitname>` replaces an existing saved kit using the player's current storage inventory, armor, offhand, and ender chest.
- `/kit give <player> <kitname>` replaces the online target's storage inventory, armor, offhand, and ender chest with the saved kit.
- `/kit list` opens a paginated browser. There is no fixed number of kit entries; use Next Page to browse additional kits.
- Kits persist in `plugins/CustomEventsPlugin/kits.yml`.

**Caution:** `/kit give` replaces the recipient's current inventory and ender chest. It does not merge items. Use it carefully.

## Not implemented yet

The following agreed features are still outstanding: single-active-event lifecycle; `/event create`, `/event manage`, `/event join`, `/event leave`; event GUIs; up to four teams and viewer-specific team visuals; event-specific kit capacity/default kit and selection/randomization; generated event identities; player-specific border and golden-hoe border tool/preview; lobby return and event inventory clearing on leave/death/disconnect/end; proximity activity messages; personal chat toggle; full advancement progress blocking; Orbital Dripstone Cannon. The Orbital Wolf Cannon is excluded from the project.

Do not install this early build as the finished server plugin.

## Requirements

- Paper 1.21.11
- JDK 21
- Maven 3.9+
- Git (for GitHub)

## 1. Install Java 21

Install a JDK 21 distribution (for example, Eclipse Temurin 21). Open PowerShell and run:

```powershell
java -version
```

Confirm the version is 21. If Windows says the command is not recognized, install the JDK and reopen PowerShell. Check `JAVA_HOME` and PATH if it still fails.

## 2. Install Maven

Install Apache Maven 3.9 or newer and add its `bin` folder to PATH. Open a new PowerShell window and run:

```powershell
mvn -version
```

Confirm Maven is using Java 21.

## 3. Extract the project

Extract `CustomEventsPlugin.zip`. Open PowerShell in the extracted `CustomEventsPlugin` folder, the folder that directly contains `pom.xml`.

Example:

```powershell
cd "$HOME\Downloads\CustomEventsPlugin"
```

## 4. Build the JAR locally

Run:

```powershell
mvn clean package
```

The first build downloads the Paper API and Maven dependencies, so internet access is required. On success, Maven reports `BUILD SUCCESS` and creates:

```text
target\CustomEventsPlugin.jar
```

If the build fails, copy the full error beginning at the first `[ERROR]`. Do not install a failed build.

## 5. Create the GitHub repository

1. Sign in to GitHub.
2. Create a repository named `CustomEventsPlugin`.
3. Choose public or private.
4. Create it empty. Do not add a README or `.gitignore` through GitHub; both are already in this project.
5. Copy the repository's HTTPS URL.

## 6. Upload the project to GitHub

In PowerShell, from the project folder, run:

```powershell
git init
git add .
git commit -m "Add Custom Events Plugin project"
git branch -M main
git remote add origin https://github.com/YOUR-USERNAME/CustomEventsPlugin.git
git push -u origin main
```

Replace `YOUR-USERNAME` with your GitHub username and use the exact URL of your repository. GitHub may prompt you to authenticate in a browser.

If `origin` already exists, use:

```powershell
git remote set-url origin https://github.com/YOUR-USERNAME/CustomEventsPlugin.git
git push -u origin main
```

## 7. GitHub Actions workflow

The repository includes `.github/workflows/build.yml`. On pushes and pull requests targeting `main`, GitHub Actions:
1. Checks out the repository.
2. Installs Temurin Java 21.
3. Caches Maven dependencies.
4. Runs `mvn clean package`.
5. Uploads `target/CustomEventsPlugin.jar` as a workflow artifact.

To download it: open the GitHub repository, select **Actions**, open the latest **Build CustomEventsPlugin** run, and download the artifact named **CustomEventsPlugin**.

The workflow artifact is not a GitHub Release. Create a release only after the plugin is implemented and tested.

## 8. Install on a test server

Do not install an early development build on your main production server.

1. Stop a Paper 1.21.11 test server.
2. Copy `target\CustomEventsPlugin.jar` into the server's `plugins` folder.
3. Start the server.
4. Check the console for plugin startup errors.
5. Kit data is stored in `plugins/CustomEventsPlugin/kits.yml`.

## 9. Kit commands

### Create a kit

Arrange your inventory and armor, place the items you want saved into your ender chest, then run:

```text
/create kit Starter
```

The saved kit includes storage inventory, armor, offhand, and ender chest. Your current items stay with you.

### Update a kit

Arrange the replacement contents and run:

```text
/kit update Starter
```

This overwrites the saved kit.

### Give a kit

```text
/kit give PlayerName Starter
```

The target must be online. Their storage inventory, armor, offhand, and ender chest are replaced by the saved kit.

### Browse saved kits

```text
/kit list
```

The menu has pages rather than a fixed kit-slot limit.

## 10. Permissions

| Permission | Default | Current status |
|---|---|---|
| `customevents.kit.create` | op | Implemented |
| `customevents.kit.update` | op | Implemented |
| `customevents.kit.give` | op | Implemented |
| `customevents.event.create` | op | Reserved |
| `customevents.event.manage` | op | Reserved |
| `customevents.event.end` | op | Reserved |
| `customevents.event.configure` | op | Reserved |
| `customevents.event.teams` | op | Reserved |
| `customevents.event.kits` | op | Reserved |
| `customevents.event.border` | op | Reserved |
| `customevents.weapon.give` | op | Reserved |
| `customevents.chat.toggle` | op | Reserved |

## 11. GitHub Actions workflow source

The workflow is already included at `.github/workflows/build.yml`. Its source is:

```yaml
name: Build CustomEventsPlugin
on:
  push:
    branches: [main]
  pull_request:
    branches: [main]
  workflow_dispatch:
permissions:
  contents: read
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '21'
          cache: maven
      - name: Build
        run: mvn --batch-mode --no-transfer-progress clean package
      - uses: actions/upload-artifact@v4
        with:
          name: CustomEventsPlugin
          path: target/CustomEventsPlugin.jar
          if-no-files-found: error
```

## 12. Making later changes

After editing the project:

```powershell
git add .
git commit -m "Describe the change"
git push
```

GitHub Actions automatically builds the new commit. Download the new artifact from the corresponding Actions run and test it on a test server.
