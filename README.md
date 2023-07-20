# More Extension Blueprint

This blueprint includes 3 examples how to develop custom actions, triggers and observations for the More Platform.
The CallWebhookAction allows to specify a url that is callen with a HTTP POST request.
The PeriodicTrigger allows to speficy trigger that selects a random participant each configurable period.

## Build Blueprints
1. Install Java 17 and set as default
2. Checkout the blueprint code
3. Checkout the [Study Manager Backend](https://github.com/MORE-Platform/more-studymanager-backend)
4. Install the `io.redlink.more:studymanager-core:1.0.0-SNAPSHOT`on your MavenLocal: Run `./mvnw clean install -Dmaven.install.skip=false -DskipTests` in the `more-studymanager-backend` directory.
5. Build blueprints and install locally: Run `./mvnw clean install` in the `more-extension-blueprint` directory.

## Enable Blueprint Extension in Studymanager
Open to `more-studymanager-backend/studymanager/pom.xml)` and insert a dependency. For all blueprint examples add:
```
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
```
Run the `more-studymanager-backend`, the extension is now available.

## How to read the document
Most things are described as comments within the code. The documentation is meant to be read in this order:

1. CallWebhookActionFactory: learn what a factory is
2. CallWebhookAction: learn how configuration and action execution works
3. PeriodicTrigger: learn hot to use sdk

## More Action Blueprint
The More Action Blueprint can found in the `more-action-extension` directory. The example shows a simple webhook caller action.
Like any extension, the code includes (at least) two classes: an action that extends `Action<ActionProperties>` and a factory, that extends
`ActionFactory<...Action, ActionProperties>`. Find infos about the implementation in these files.

If you want to test the webhook locally we prepared a simple webserver, that establishes a webhook at localhost. Just go to
`development/webhook-example` and run `node webhook.js`.

## More Trigger Blueprint
The More Trigger Blueprint can be found in the `more-trigger-extension` directory. It provides a periodic trigger, that 
randomly selects a participant and triggers the actions of the interventions at every period.

## More Observation Blueprint
This Blueprint can be found in the `more-observation-extension` directory. The observation can be used to create and configure
an observation for the `PushButton` observation. Note, this observation does not use any sensors but is a good starting point.
The extension app can be found in [More APP - Blueprint Branch](https://github.com/MORE-Platform/more-app-multiplatform/tree/more-app-blueprint).
