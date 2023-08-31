### Defining CI/CD for Building and Deploying Extension Blueprints using Github Packages:
In order to establish a Continuous Integration and Continuous Deployment (CI/CD) process for building and deploying extension blueprints via Github Packages, 
and since this project relies on the `io.redlink.more:studymanager-core` artifact as a necessary dependency. Thus, the initial task involves creating this dependency as a  standalone artifact, in github packages.
#### Publishing a package
The following steps need to be undertaken within the more-studymanager-backend code-base project:
##### Step 1: Prepare Distribution Management
To make the studymanager-core project artifact available as a separate package, the distribution management configuration needs to be added to the pom.xml file of the studymanager-core project. Insert the following code snippet:
```xml
<distributionManagement>
    <repository>
        <id>github</id>
        <name>lbi-dhp-studymanager-core</name>
        <url>https://maven.pkg.github.com/LBI-DHP/more-studymanager-backend</url>
    </repository>
</distributionManagement>
```
Make sure to adjust the URL in the <url> tag according to the following pattern: https://maven.pkg.github.com/OWNER/REPOSITORY, where OWNER is the account name of the user or organization that owns the repository, and REPOSITORY is the name of the repository housing the project.
#####Step 2: Modify studymanager-core/pom.xml
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
##### Step 3: Configure Github Action
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


#####Step 4: Create a Private Access Token
You need an access token to write the package and also read other packages. You can use a personal access token (PAT) to authenticate to GitHub Packages or the GitHub API. When you create a personal access token, you can assign the token different scopes depending on your needs.
You can generate a new personal access token under Profile/Settings/Developer settings/Personal access tokens.
Select the write:packages scope.
[![write-packages](a "write-packages")](http://google.com "write-packages")
Copy the token value as you will need it in the next step.
#####Step 5: Add secret key to the repository
In `more-extension-blueprint` project repository you need to create a secret using the token generated in the previous step. Go to Settings/Secrets and create a new repository secret.
[![secret-key](a "secret key")](http://google.com "secret-key")
