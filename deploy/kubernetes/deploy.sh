#!/bin/bash

# Login to the Kubernetes cluster before launching this script

# mvn clean package -Dquarkus.kubernetes.deploy=true -Dquarkus.native.container-build=true -Dnative

mvn clean package -DskipTests -Dquarkus.container-image.build=true
