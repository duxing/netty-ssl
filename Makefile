.DEFAULT_GOAL := docker_build

CUR_DIR := $(shell dirname $(realpath $(firstword $(MAKEFILE_LIST))))
REPO_DIR := $(shell dirname $(realpath $(lastword $(MAKEFILE_LIST))))

DIR_NAME := $(shell basename $(REPO_DIR))
SERVICE_NAME := netty-ssl
IMAGE_TAG ?= latest

JAVA11 ?= false

# todo: maybe consider using mustache to consolidate 2 dockerfiles into 1.
ifeq ($(JAVA11),true)
GRADLE_IMAGE_TAG := jdk11
DOCKER_FILE := Dockerfile.java11
else
GRADLE_IMAGE_TAG := jdk8
DOCKER_FILE := Dockerfile.java8
endif

ifeq ($(CI),true)
GRADLE := docker run --rm -u root -v $(CUR_DIR):/app -v /var/run/docker.sock:/run/docker.sock -e GRADLE_OPTS=-Dorg.gradle.daemon=false -w /app gradle:$(GRADLE_IMAGE_TAG) gradle
else
# todo: xdu. switch this dynamically
GRADLE := ./gradlew
endif

.PHONY: build
build:
	$(GRADLE) clean build -x test

.PHONY: docker_build
docker_build: build
	docker build -f $(REPO_DIR)/$(DOCKER_FILE) -t $(SERVICE_NAME):$(IMAGE_TAG) $(REPO_DIR)

.PHONY: run
run: docker_build
	docker run --rm --init $(SERVICE_NAME):$(IMAGE_TAG)

