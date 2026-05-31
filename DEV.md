# Development Notes

## Prerequisites

- Java 21
- `npm`

The repository includes the Gradle wrapper, so a separate Gradle installation is not required. Use `./gradlew` from the repository root.

## Build and Test

**Build the project** - Runs the standard Gradle build lifecycle for the project. This includes compilation, tests, and packaging.

```shell
./gradlew build
```

**Build the jar only** - Builds the extension archive without running the full build lifecycle.

```shell
./gradlew jar
```

**Run tests** - Runs the full test suite.

```shell
./gradlew test
```

**Run a selected test** - Runs a single test class.

```shell
./gradlew test --tests marker.rule.RequestRuleTest
./gradlew test --tests marker.rule.ResponseRuleTest
```

## Running Extension

Build the jar with:

```shell
./gradlew build
```

or:

```shell
./gradlew jar
```

The extension is saved as `build/libs/burp-marker-0.0.0-SNAPSHOT.jar`. This resulting archive can be added to Burp via the Extension tab.

## Remote Debugging

Start Burp Suite with JDWP enabled:

```shell
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar "/path/to/burpsuite.jar"
```

Connect to the socket using your dev tool. For IntelliJ IDE, remote debugging can be set up in Run / Debug Configurations > Edit Configurations > Add New Configuration > Remote JVM Debug. IntelliJ pre-populates command line arguments with `-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005`. Save the configuration and hit Debug.

## Workflow

Development is expected to follow this branch flow:

- work is done on dedicated feature branches
- feature branches are squash-merged into `develop`
- `develop` is merged into `main`

Pushing directly to `main` and `develop` is forbidden.

Changes are incorporated via pull requests. Repository branch protection is configured so that pull requests targeting protected branches must be up to date before merging.

Head branches are automatically deleted after pull requests are merged. Therefore, expect feature branches to disappear after merge.

Typical workflow:

```shell
# clone the repository
git clone <repo-url>
cd burp-marker

# create a feature branch from develop
git switch -c feature/some-change develop

# implement changes and commit
git commit -m "feat: some new feature"

# update local refs before rebasing
git fetch origin

# rebase the feature branch onto the latest develop
git rebase origin/develop

# push the branch
git push -u origin feature/some-change

# if the feature branch was already pushed before the rebase
git push --force-with-lease
```

## Commit Message Linting

### Release Semantics

Commit messages are expected to follow the [Conventional Commits](https://www.conventionalcommits.org/) format as release management is handled by [semantic-release](https://github.com/semantic-release/semantic-release/tree/master). The release behavior for commit types is defined in [.releaserc.json](./.releaserc.json). From the current release configuration:

- `feat` triggers a minor release
- `fix` triggers a patch release
- `perf` triggers a patch release
- breaking changes trigger a major release
- `build`, `chore`, `ci`, `docs`, `refactor`, `revert`, `style`, and `test` do not trigger a release

Breaking changes also include headers that use the `!` marker, for example:

- `feat!: some incompatible feature`
- `fix!: some incompatible fix`

Commits containing `[skip release]` or `[release skip]` [are excluded](https://github.com/semantic-release/semantic-release/blob/master/docs/support/FAQ.md#can-i-exclude-commits-from-the-analysis) from semantic-release commit analysis and do not participate in release type determination.

### Local Setup

[commitlint](https://commitlint.js.org/) can be configured to enforce message format during development. The [local setup](https://commitlint.js.org/guides/local-setup.html) guide explains the installation and configuration.

The guide uses Husky for Git hook management, which might be unnecessary if no other Git hooks are used. Therefore, the repository contains the script `./.commitlint/configure-commitlint.sh`, which installs the required commitlint packages locally through `npm` and configures Git to use `.commitlint/` as the hooks directory. Usage:

```shell
# run the setup script
./.commitlint/configure-commitlint.sh

# non-compliant messages should now be rejected
git commit -m 'invalid: should be refused by commitlint' --allow-empty

# ⧗   --- input ---
# invalid: should be refused by commitlint
# ✖   type must be one of [build, chore, ci, docs, feat, fix, perf, refactor, revert, style, test] [type-enum]

# ✖   found 1 problems, 0 warnings
# ⓘ   Get help: https://github.com/conventional-changelog/commitlint/#what-is-commitlint
```
