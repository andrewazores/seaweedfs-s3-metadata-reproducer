#!/usr/bin/env bash

podman rmi mystorage:latest

docker build --build-arg SEAWEED_VERSION="${1:-dev}" -t mystorage:latest storage -f storage/Dockerfile

./mvnw clean test
