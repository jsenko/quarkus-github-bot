#! /bin/bash

# login to the OpenShift cluster before launching this script

# delete problematic image
oc delete is ubi-quarkus-native-binary-s2i

# switch to the right project
oc project prod-quarkus-bot

mvn clean package -Dquarkus.kubernetes.deploy=true -Dquarkus.native.container-build=true -Dnative

# add kubernetes.io/tls-acme: 'true' to the route to renew the SSL certificate automatically
