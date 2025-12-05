#!/usr/bin/env bash


if [ -z "${SKIP_BUILD}" ]; then
    podman rmi mystorage:latest
    if [ -z "${SOURCE_BUILD}" ]; then
      docker build --build-arg SEAWEED_VERSION="${1:-dev}" -t mystorage:latest storage -f storage/Dockerfile
    else
      docker build --no-cache --build-arg SEAWEED_VERSION="${1:-master}" -t mystorage:latest storage -f storage/Dockerfile.source
    fi
fi

./mvnw -Dhttp="${HTTP:-apache}" clean test -Dmode="${MODE:-tag}"
