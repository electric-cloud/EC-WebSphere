EC-WebSphere
============

The ElectricCommander WebSphere integration

## Compile ##

Run gradlew to compile the plugin

`./gradlew`

## Tests ##

#### Creating ecplugin.properties ####
Create an ecplugin.properties at the root of this repository with the following content

    COMMANDER_USER=<COMMANDER_USER>
    COMMANDER_PASSWORD=<COMMANDER_PASSWORD>
    
These represent secrets that **should not** be checked in.

#### Running tests ####
Run the `test` task to run the system tests. You may want to specify the ElectricFlow Server to test against by way of the COMMANDER_SERVER environment variable.

`COMMANDER_SERVER=<ElectricFlow Server FQDN or IP> ./gradlew test`
