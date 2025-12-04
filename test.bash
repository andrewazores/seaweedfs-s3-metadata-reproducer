#!/usr/bin/env bash

podman rmi mystorage:latest

if [ -z "${SOURCE_BUILD}" ]; then
  docker build --build-arg SEAWEED_VERSION="${1:-dev}" -t mystorage:latest storage -f storage/Dockerfile
else
  docker build --build-arg SEAWEED_VERSION="${1:-master}" -t mystorage:latest storage -f storage/Dockerfile.source
fi

./mvnw clean test -Dmode="${MODE:-tag}"
