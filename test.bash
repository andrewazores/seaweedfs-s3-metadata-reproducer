#!/usr/bin/env bash

podman rmi mystorage:latest

docker build --build-arg SEAWEED_VERSION="${1:-4.01}" -t mystorage:latest storage -f storage/Dockerfile

./mvnw clean test
