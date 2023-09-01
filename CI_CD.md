# Defining CI/CD for Building and Deploying Extension Blueprints using Github Packages:
In order to establish a Continuous Integration and Continuous Deployment (CI/CD) process for building and deploying extension blueprints via Github Packages, 
and since this project relies on the `io.redlink.more:studymanager-core` artifact as a necessary dependency. Thus, the initial task involves creating this dependency as a  standalone artifact, in github packages.
## Publishing a package
The following steps need to be undertaken within the more-studymanager-backend code-base project:
### Step 1: Prepare Distribution Management
To make the studymanager-core project artifact available as a separate packages, the distribution management configuration needs to be added to the pom.xml file of the studymanager-core project. Insert the following code snippet:
```xml
<distributionManagement>
    <repository>
        <id>github</id>
        <name>lbi-dhp-studymanager-core</name>
        <url>https://maven.pkg.github.com/MORE-Platform/more-studymanager-backend</url>
    </repository>
</distributionManagement>
```
Make sure to adjust the URL in the <url> tag according to the following pattern: https://maven.pkg.github.com/OWNER/REPOSITORY, where OWNER is the account name of the user or organization that owns the repository, and REPOSITORY is the name of the repository housing the project.
### Step 2: Modify studymanager-core/pom.xml
The studymanager-core/pom.xml file inherits from the main pom file located in the parent folder. In this file, deployment of Maven plugins is skipped by default. To enable the necessary deployment configurations, follow these steps:
1. Override the build element in the studymanager-core/pom.xml file.
2. Set the skip attribute to false for the Maven Deploy Plugin.

```xml
<build>
    <pluginManagement>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-deploy-plugin</artifactId>
                <version>3.1.1</version>
                <configuration>
                    <skip>false</skip>
                </configuration>
            </plugin>
        </plugins>
    </pluginManagement>
</build>
```
By making these changes, you ensure that the Maven Deploy Plugin is no longer skipped, allowing for the proper deployment of the artifact.
### Step 3: Configure Github Action
Edit configuration file `compile-test.yml` under `$PROJECT_ROOT/.github/workflows/`
The compile-and-Test job was edited and  `server-id`, `server-password` and `GITHUB_TOKEN_REF` were added.
```yaml
Compile-and-Test:
    name: Compile and Test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          distribution: 'temurin'
          java-version: 17
          server-id: github # value of repository/id field of the pom.xml
          server-password: GITHUB_TOKEN_REF # env variable name for GitHub Personal Access Token
      - name: Compile and test project
        run: ./mvnw -B -U
          --no-transfer-progress
          compile test          
        env:
          GITHUB_TOKEN_REF: ${{ secrets.GITHUB_TOKEN }}
      - name: Show 3rd-Party Licenses
        run: |
          cat ./studymanager/target/generated-sources/license/THIRD-PARTY.txt
      - name: Upload Test Results
        if: always()
        uses: actions/upload-artifact@v3
        with:
          name: Test Results
          path: "**/TEST-*.xml"
      - name: Upload Licenses List
        if: always()
        uses: actions/upload-artifact@v3
        with:
          name: Licenses List
          path: "./studymanager/target/generated-sources/license/THIRD-PARTY.txt"
```
GitHub provides a token that you can use to authenticate on behalf of GitHub Actions: GITHUB_TOKEN.
GitHub automatically creates a GITHUB_TOKEN secret to use in your workflow and you can use it to authenticate in a workflow run.
Now, when any change is pushed to master a new package is created and published to GitHub Packages.
## Accessing and Installing the package
Whenever the studymanager project is built, the `studymanager-core` artifact is automatically built as well.
The artifact is available in the packages list of `more-studymanager-backend`:
[io.redlink.more.studymanager-core ](https://github.com/MORE-Platform/more-studymanager-backend/packages/ "io.redlink.more.studymanager-core")

By following these steps, we will use the `studymanager-core` artifact in `more-extension-blueprint` and then deploy the following artifacts to GitHub Packages:
- `more-action-extension`
- `more-trigger-extension`
- `more-observation-extension`
### Step 1: Add the project as a dependency on the pom.xml
We can include it as a dependency in `more-extension-blueprint` project by the following elemnt.

```xml
<dependency>
  <groupId>io.redlink.more</groupId>
  <artifactId>studymanager-core</artifactId>
  <version>LATEST</version>
</dependency>
```
### Step 2: Prepare Distribution Management for all artifiacts
To make all modules in `more-extension-blueprint` project available as a separate package, the distribution management configuration needs to be added to the pom.xml file of the `more-extension-blueprint` project. Insert the following code snippet:
```yaml
<distributionManagement>
    <repository>
        <id>github</id>
        <name>lbi-dhp-more-extension-blueprint</name>
        <url>https://maven.pkg.github.com/MORE-Platform/more-studymanager-backend</url>
    </repository>
</distributionManagement>
```
Make sure to adjust the URL in the tag according to the following pattern: https://maven.pkg.github.com/OWNER/REPOSITORY, where OWNER is the account name of the user or organization that owns the repository, and REPOSITORY is the name of the repository housing the project.
### Step 3: Adjusting pom.xml for distribution management
Using the `deploy` command in CI/CD requires you to set up a new repository in the distributionManagement tag of the pom.xml. In my case I decided to enable the additional repository by using a Maven profile by adding `<Profiles>` to `pom.xml` parent file.
```yaml
<profiles>
    <profile>
        <id>github</id>
        <repositories>
            <repository>
                <id>github</id>
                <name>lbi-dhp-studymanager-core</name>
                <url>https://maven.pkg.github.com/MORE-Platform/more-studymanager-backend</url>
                <snapshots><enabled>true</enabled></snapshots>
                <releases><enabled>true</enabled></releases>
            </repository>
        </repositories>
    </profile>
</profiles>
```
### Step 4: Create a Private Access Token
You need an access token to write the package and also read other packages. You can use a personal access token (PAT) to authenticate to GitHub Packages or the GitHub API. When you create a personal access token, you can assign the token different scopes depending on your needs.
You can generate a new personal access token under 
[Profile/Settings/Developer settings/Personal access tokens](https://github.com/settings/tokens/new "Profile/Settings/Developer settings/Personal access tokens").
Select the write:packages scope. Because we are going to use these token to deploy artifacts.

![write-packeages](https://github.com/MORE-Platform/more-extension-blueprint/assets/107924035/4c8a1ac6-85e5-4c8d-a8c6-c2d5468b85a6)

Copy the token value as you will need it in the next step.
### Step 5: Add secret key to the repository
In `more-extension-blueprint` project repository you need to create a secret using the token generated in the previous step. Go to Settings/Secrets and create a new repository secret.

![secret](https://github.com/MORE-Platform/more-extension-blueprint/assets/107924035/484ee5e1-e034-4ef8-a707-a5d2b6f8bb5b)

Set a Name (E.g. GH_PAT_FOR_ACTIONS_TOKEN) and paste the token under Value.
You have to add the personal access token to your Maven`s settings.xml in .m2 folder:
```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <servers>
        <server>
            <id>github</id>
            <username>your-username</username>
            <password>your-personal-access-token</password>
        </server>
    </servers>
</settings>
```
Locally, you can now publish a new Maven artifact into the GitHub Package Registry with
`./mvnw -Pgithub deploy`
### Step 6: Configure Github Action
In `more-extension-blueprint` project, create a github action configuration file under $PROJECT_ROOT/.github/workflows/.
Here is the `compile-test.yaml` file:

```yaml
name: Test and Compile
on:
  workflow_dispatch:
  push:

jobs:
  Compile-and-Test:
    name: Compile and Test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          distribution: 'temurin'
          java-version: 17
          server-id: github # value of repository/id field of the pom.xml          
          server-password: GITHUB_TOKEN_REF # env variable name for GitHub Personal Access Token      
      - name: Build and Deploy
        run: ./mvnw -B -U clean deploy
          -Pgithub package --file pom.xml
        env:          
          GITHUB_TOKEN_REF: ${{ secrets.GH_PAT_FOR_ACTIONS_TOKEN }}
  event_file:
    name: "Event File"
    runs-on: ubuntu-latest
    steps:
      - name: Upload
        uses: actions/upload-artifact@v3
        with:
          name: Event File
          path: ${{ github.event_path }}

```
With this GitHub Actions build, all artifacts will be published and pop up in the repository`s dashboard.
## Use artifacts in MORE Study Manager Backend project
All the artifacts are now accessible on GitHub Packages, and we can include them in the pom.xml file of the studymanager project. Then, we need to update the CI/CD workflow to build based on these changes.
### Step 1: Add artifacts to pom.xml file
All artifacts are added as dependency on this path: `more-studymanager-backend/studymanager/pom.xml`
```xml
<dependency>
    <groupId>io.redlink.more</groupId>
    <artifactId>more-action-extension</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>io.redlink.more</groupId>
    <artifactId>more-trigger-extension</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>io.redlink.more</groupId>
    <artifactId>more-observation-extension</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```
### Step 2: Adjusting pom.xml for distribution management
Using the deploy command in CI/CD requires you to set up a new repository in the distributionManagement tag of the pom.xml. In this case I decided to enable the additional repository by using a Maven profile by adding <Profiles> to pom.xml parent file. (`more-studymanager-backend/pom.xml`)
```xml
<profile>
    <id>github</id>
    <repositories>
        <repository>
            <id>github</id>
            <name>lbi-dhp-more-extension-blueprint</name>
            <url>https://maven.pkg.github.com/MORE-Platform/more-studymanager-backend</url>
            <snapshots><enabled>true</enabled></snapshots>
            <releases><enabled>true</enabled></releases>
        </repository>
    </repositories>
</profile>
```
### Step 3: Configure Github Action
Edit the configuration file `compile-test.yml` located at `$PROJECT_ROOT/.github/workflows/` and add `-Pgithub package --file pom.xml` parameter for run command for Compile-and-Test job as follows:
```yaml
Compile-and-Test:
    name: Compile and Test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          distribution: 'temurin'
          java-version: 17
          server-id: github # value of repository/id field of the pom.xml
          server-password: GITHUB_TOKEN_REF # env variable name for GitHub Personal Access Token
      - name: Compile and test project
        run: ./mvnw -B -U
          --no-transfer-progress
          compile test
          -Pgithub package --file pom.xml
        env:
          GITHUB_TOKEN_REF: ${{ secrets.GITHUB_TOKEN }}
      - name: Show 3rd-Party Licenses
        run: |
          cat ./studymanager/target/generated-sources/license/THIRD-PARTY.txt
      - name: Upload Test Results
        if: always()
        uses: actions/upload-artifact@v3
        with:
          name: Test Results
          path: "**/TEST-*.xml"
      - name: Upload Licenses List
        if: always()
        uses: actions/upload-artifact@v3
        with:
          name: Licenses List
          path: "./studymanager/target/generated-sources/license/THIRD-PARTY.txt"
```
