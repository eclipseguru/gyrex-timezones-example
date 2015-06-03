# Sample Application for Gyrex

This sample application shows time in different timezones.

## Implementation Notes

* Using Yeoman to generate Angular UI stub; from there on it's Grunt, Bower
* Using JAX-RS (powered by Jersey) for the REST API
* Using Jetty inside Equinox OSGi as container
* No persistence implemented; users and timezones are only stored in memory and do not survive back-end restarts
* services approach is used; persistence can be provided by different service implementation
* backend is stateless
* users are issued authentication tokens with an expiration time
* tokens are currently not renewed, thus after a few minutes users are forced to re-login


## Setup Instructions

* For Backend you need Eclipse with PDE (Plug-In Development Environment)
* Import all projects into Eclipse
* Set target platform (in releng folder)
* For Frontend you need npm, node.js, Bower and Grunt
